-- Remove the unique constraint that prevents multiple reviews from same user for same property
ALTER TABLE property_reviews DROP CONSTRAINT property_reviews_property_id_user_id_key; 