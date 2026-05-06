package com.nearprop.service;

import com.nearprop.entity.RoleRequest;

/**
 * Service for sending role-related email notifications
 */
public interface RoleEmailService {
    
    /**
     * Sends a notification email when a user submits a role request
     * 
     * @param roleRequest The role request entity
     */
    void sendRoleRequestSubmittedNotification(RoleRequest roleRequest);
    
    /**
     * Sends a notification email when a role request is approved
     * 
     * @param roleRequest The role request entity that was approved
     */
    void sendRoleRequestApprovedNotification(RoleRequest roleRequest);
    
    /**
     * Sends a notification email when a role request is rejected
     * 
     * @param roleRequest The role request entity that was rejected
     */
    void sendRoleRequestRejectedNotification(RoleRequest roleRequest);
} 