# Background Image Setup

The code has been updated to support a background image. Follow these steps:

## 1. Save the background image
- Take the landscape image you provided
- Save it as `background.png` in the `src/main/resources/` folder
- File location: `src/main/resources/background.png`
- Recommended image size: 800×600px or larger for better quality

## 2. Rebuild the project
```bash
mvn clean package
mvn javafx:run
```

## 3. Features implemented
- ✅ Background image loads from `src/main/resources/background.png`
- ✅ **Parallax effect**: Background scrolls at 30% camera speed for depth
- ✅ Image tiles horizontally to fill the screen width
- ✅ Fallback to dark blue if image is missing (graceful degradation)

## 4. Customization
To adjust parallax speed, edit Engine.java:
```java
double parallaxCameraX = cameraX * 0.3; // Change 0.3 to desired value (0.0-1.0)
```
- `0.3` = 30% parallax (slow, distant effect)
- `0.5` = 50% parallax (medium)
- `1.0` = 100% parallax (moves with camera like ground)
