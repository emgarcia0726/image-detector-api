# image-detector-api

This API will ingest user images, analyze them for object detection using Imagga's API, and store the Image content in a SQL database.

This API has 3 GET endpoints and 1 POST endpoint. The user can retrieve all images that are in the database or filter based on the objects detected
 or retrieve only one image with its unique identifier. Object detection is only ran if indicated when adding an image.

