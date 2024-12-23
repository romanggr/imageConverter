# Image Converter to .webp

A simple Java application that converts images from JPG, JPEG, and PNG formats to WebP.
The app also handles copying existing WebP files from the input directory to the output directory 
and moves unsupported files to a separate folder. 

## Features

- Converts `.jpg`, `.jpeg`, and `.png` files to `.webp`.
- Copies `.webp` files directly from the input folder to the output folder.
- Moves unsupported image files (non `.jpg`, `.jpeg`, `.png`, `.webp`) to a separate folder named `unchangeableFiles`.
- Multi-threaded processing to speed up the image conversion process by processing multiple files concurrently.

## Requirements
- Java 8 or higher
- Maven (for dependencies)

## Libraries Used
- [Scrimage](https://github.com/sksamuel/scrimage) for WebP conversion.

## Setup
### 1. Clone the repository
```bash
git clone https://github.com/yourusername/image-converter.git
cd image-converter
```

### 2. Install dependencies
The project uses Maven, so you can install dependencies by running:

```bash
mvn install
```

### 3. Paste your files
Paste your files in "src/main/resources/input-files/"

### 4. Run the application
Run the application using:
```bash
mvn exec:java
```