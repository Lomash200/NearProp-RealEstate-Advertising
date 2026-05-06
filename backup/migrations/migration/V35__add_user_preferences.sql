-- Create user_preferences table for multilingual support and other user preferences
CREATE TABLE user_preferences (
    user_id BIGINT PRIMARY KEY,
    language VARCHAR(10),
    email_notifications BOOLEAN DEFAULT true,
    sms_notifications BOOLEAN DEFAULT true,
    app_notifications BOOLEAN DEFAULT true,
    temperature_unit VARCHAR(5) DEFAULT 'C',
    distance_unit VARCHAR(5) DEFAULT 'km',
    currency VARCHAR(3) DEFAULT 'INR',
    date_format VARCHAR(20) DEFAULT 'dd/MM/yyyy',
    time_format VARCHAR(10) DEFAULT 'HH:mm',
    theme VARCHAR(20) DEFAULT 'light',
    dashboard_view VARCHAR(20) DEFAULT 'standard',
    property_view_type VARCHAR(20) DEFAULT 'grid',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert default preferences for existing users
INSERT INTO user_preferences (
    user_id, language, email_notifications, sms_notifications, 
    app_notifications, temperature_unit, distance_unit, currency
)
SELECT 
    id, 'en', true, true, 
    true, 'C', 'km', 'INR'
FROM users;

-- Create index for faster lookups
CREATE INDEX idx_user_preferences_language ON user_preferences(language); 