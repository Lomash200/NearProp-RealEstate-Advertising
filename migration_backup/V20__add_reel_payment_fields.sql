-- Add payment related columns to property_reels table
ALTER TABLE property_reels 
  ADD COLUMN payment_required BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN payment_transaction_id VARCHAR(255) NULL; 