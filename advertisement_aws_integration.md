# Advertisement AWS S3 Integration

## Overview

This implementation updates the advertisement media handling system to store all media files (images and videos) in AWS S3 with a structured folder hierarchy. The folder structure is designed to organize media by role, advertiser information, and advertisement title.

## Implemented Changes

1. Updated `AdvertisementServiceImpl.processFileUploads()` method to:
   - Create a structured folder path based on user role, advertiser ID/name, and advertisement title
   - Store images and videos in separate sub-folders
   - Use the S3Service for uploading media instead of local storage

2. Added `advertisementFolder` property to `AwsConfig.S3Config` class to define the base folder for advertisement media.

3. Updated the AWS S3 integration to maintain the following folder structure:
   ```
   media/advertisements/{role}/{advertiser_id}_{advertiser_name}/{ad_title}/images/
   media/advertisements/{role}/{advertiser_id}_{advertiser_name}/{ad_title}/videos/
   ```

4. Used existing `S3Service.uploadAdvertisementMedia()` method to handle the upload of media files to the appropriate AWS S3 path.

## Benefits

1. **Organized Storage**: Media files are organized in a logical folder structure making management easier.

2. **Role-Based Organization**: Files are grouped by user role (admin, advisor, developer, seller, franchisee, user).

3. **Advertiser-Specific Organization**: Each advertiser's files are stored in dedicated folders with their ID and name.

4. **Advertisement-Specific Organization**: Media for each advertisement is stored in its own folder.

5. **Separation of Media Types**: Images and videos are stored in separate sub-folders.

6. **Scalability**: AWS S3 provides reliable, scalable storage for all media files.

7. **Consistent Naming**: All file names include unique identifiers to avoid conflicts.

## Configuration

The system is configured to use AWS S3 for storage, with fallback to local storage if AWS credentials are not provided. 

AWS configuration values are set in the `application.yml` file:

```yaml
aws:
  access-key: ${AWS_ACCESS_KEY:}
  secret-key: ${AWS_SECRET_KEY:}
  region: ${AWS_REGION:ap-south-1}
  s3:
    bucket: ${S3_BUCKET:nearprop-documents}
    advertisement-folder: advertisements
```

## Usage

No changes are needed to the way advertisements are created or updated. The system automatically stores media files in the appropriate AWS S3 location with the structured folder hierarchy. 