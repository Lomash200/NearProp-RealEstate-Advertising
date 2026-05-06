package com.nearprop.advertisement.service;

import com.nearprop.advertisement.entity.Advertisement;

public interface AdvertisementEmailService {
    
    /**
     * Send notification email about advertisement expiring in one day
     * @param advertisement Advertisement that will expire in one day
     * @return True if email was sent successfully
     */
    boolean sendDayBeforeExpirationNotification(Advertisement advertisement);
    
    /**
     * Send notification email about advertisement expiring in five hours
     * @param advertisement Advertisement that will expire in five hours
     * @return True if email was sent successfully
     */
    boolean sendHoursBeforeExpirationNotification(Advertisement advertisement);
    
    /**
     * Send notification email about advertisement that has expired
     * @param advertisement Advertisement that has expired
     * @return True if email was sent successfully
     */
    boolean sendExpirationNotification(Advertisement advertisement);
} 