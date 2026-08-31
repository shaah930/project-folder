package com.kallista.core.geojson.services.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerException;
import com.adobe.granite.taskmanagement.TaskManagerFactory;
import com.adobe.granite.workflow.exec.InboxItem;
import com.kallista.core.geojson.constants.GeoJsonPipelineConstants;
import com.kallista.core.geojson.services.NotificationService;

@Component(service = NotificationService.class)
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private static final String FALLBACK_ASSIGNEE = "administrators";
    private static final Set<String> INVALID_ASSIGNEES = Set.of("unknown", "anonymous", "kohlersysuser");
    // Task property names must not use an unregistered JCR namespace prefix.
    private static final String PROP_ASSET_PATH = "kallistaAssetPath";
    private static final String PROP_CORRELATION_ID = "kallistaCorrelationId";
    private static final String PROP_OUTPUT_PATH = "kallistaOutputPath";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void notifyValidationFailure(String uploader, String assetPath, String invalidRowsSummary, String correlationId) {
        String details = invalidRowsSummary == null ? "" : invalidRowsSummary.lines().collect(Collectors.joining(" | "));
        LOG.warn("geojson validation failed. uploader={} assetPath={} correlationId={} errors={}", uploader, assetPath,
                correlationId, details);
        createInboxTask(uploader, "GeoJSON validation failed",
                String.format("Validation failed for %s.%nError: %s", assetPath, details), assetPath, correlationId,
                null, InboxItem.Priority.HIGH);
    }

    @Override
    public void notifySuccess(String uploader, String assetPath, String outputPath, String correlationId) {
        LOG.info("geojson generated successfully. uploader={} assetPath={} outputPath={} correlationId={}", uploader,
                assetPath, outputPath, correlationId);
        createInboxTask(uploader, "GeoJSON generated successfully",
                String.format("GeoJSON was generated from %s and stored at %s.", assetPath, outputPath), assetPath,
                correlationId, outputPath, InboxItem.Priority.LOW);
    }

    @Override
    public void notifyFailure(String uploader, String assetPath, String message, String correlationId) {
        LOG.error("geojson processing failed. uploader={} assetPath={} correlationId={} message={}", uploader,
                assetPath, correlationId, message);
        createInboxTask(uploader, "GeoJSON processing failed",
                String.format("Processing failed for %s.%nReason: %s", assetPath, message), assetPath, correlationId,
                null, InboxItem.Priority.HIGH);
    }

    private void createInboxTask(String uploader, String title, String instructions, String assetPath,
            String correlationId, String outputPath, InboxItem.Priority priority) {
        try (ResourceResolver resolver = getServiceResolver()) {
            TaskManager taskManager = resolver.adaptTo(TaskManager.class);
            if (taskManager == null) {
                LOG.warn("unable to adapt resolver to TaskManager, skipping inbox task for assetPath={}", assetPath);
                return;
            }

            TaskManagerFactory taskManagerFactory = taskManager.getTaskManagerFactory();
            String assignee = resolveAssignee(uploader);
            try {
                createTask(taskManager, taskManagerFactory, assignee, title, instructions, assetPath, correlationId,
                        outputPath, priority);
            } catch (TaskManagerException ex) {
                if (FALLBACK_ASSIGNEE.equals(assignee)) {
                    throw ex;
                }
                LOG.warn("inbox task rejected for assignee={}, retrying with {}", assignee, FALLBACK_ASSIGNEE, ex);
                createTask(taskManager, taskManagerFactory, FALLBACK_ASSIGNEE, title, instructions, assetPath,
                        correlationId, outputPath, priority);
            }
            LOG.debug("created inbox task assetPath={} correlationId={}", assetPath, correlationId);
        } catch (LoginException | TaskManagerException ex) {
            LOG.error("unable to create inbox task for assetPath={} correlationId={}", assetPath, correlationId, ex);
        }
    }

    private void createTask(TaskManager taskManager, TaskManagerFactory taskManagerFactory, String assignee,
            String title, String instructions, String assetPath, String correlationId, String outputPath,
            InboxItem.Priority priority) throws TaskManagerException {
        Task task = taskManagerFactory.newTask(Task.DEFAULT_TASK_TYPE);
        task.setName(title);
        task.setDescription(instructions);
        task.setInstructions(instructions);
        task.setContentPath(assetPath);
        task.setCurrentAssignee(assignee);
        task.setPriority(priority);
        task.setProperty(PROP_ASSET_PATH, assetPath);
        if (correlationId != null) {
            task.setProperty(PROP_CORRELATION_ID, correlationId);
        }
        if (outputPath != null) {
            task.setProperty(PROP_OUTPUT_PATH, outputPath);
        }
        taskManager.createTask(task);
    }

    // TaskManager rejects an ownerId that is not an existing authorizable.
    private String resolveAssignee(String uploader) {
        if (StringUtils.isBlank(uploader) || INVALID_ASSIGNEES.contains(uploader)) {
            return FALLBACK_ASSIGNEE;
        }
        return uploader;
    }

    private ResourceResolver getServiceResolver() throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(
                Map.of(ResourceResolverFactory.SUBSERVICE, GeoJsonPipelineConstants.SUBSERVICE_NAME));
    }
}