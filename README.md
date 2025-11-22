 # 🎥 EdgeVisionRT - Real-Time Edge Detection Viewer

Android app with native C++ edge detection, OpenGL ES 2.0 rendering, and TypeScript web viewer.

---
## ✅ Features Implemented (Android & Web)

### Android Features
- ✅ **Camera2 API integration** - Real-time camera feed capture
- ✅ **Native C++ edge detection** - Custom Sobel filter via JNI/NDK
- ✅ **OpenGL ES 2.0 rendering** - Hardware-accelerated display with shaders
- ✅ **Toggle functionality** - Switch between raw camera feed and edge-detected output
- ✅ **FPS counter** - Real-time performance metrics (15-20 FPS)
- ✅ **Processing time display** - Shows ~20ms per frame
- ✅ **Portrait mode optimization** - Correct camera orientation
- ✅ **Lifecycle management** - Proper pause/resume handling

### Web Features
- ✅ **TypeScript implementation** - Fully typed, modular code
- ✅ **Side-by-side comparison** - Raw frame vs edge-detected frame
- ✅ **Frame statistics display** - Resolution, FPS, processing algorithm
- ✅ **Static frame viewer** - Displays pre-captured processed frames


### 📷 Screenshots & GIF
## Screenshot
https://github.com/user-attachments/assets/1b69affc-c3a2-4784-b42b-77670020bd8b
https://github.com/user-attachments/assets/a1ddd3c2-a8a6-4b37-ac9d-60706e665847

## GIF
https://github.com/user-attachments/assets/07534372-9a8f-412e-8cd0-ce521083a00a

### Android App

**Raw Camera Feed:**
- Normal colored camera view
- Real-time capture at 640×480 resolution
- Toggle button shows "EDGE DETECTION"

**Edge Detection Output:**
- White edges on black background
- Sobel filter applied in native C++
- Processed in ~20ms per frame
- Toggle button shows "RAW FEED"

**UI Elements:**
- Top bar: FPS counter, resolution, processing time
- Toggle button: Switch between modes
- Full-screen OpenGL surface view

### Web Viewer

**Layout:**
-https://github.com/user-attachments/assets/ea87d51e-8982-4bf7-8292-9bbd9ac6920b
-https://github.com/user-attachments/assets/da76dd61-bae1-499c-91ae-754e71a547a6


## ⚙ Setup Instructions

### Prerequisites
- Android Studio Ladybug or later
- Android SDK (API 24+)
- NDK (Side by side) 
- CMake 3.22.1
- Node.js 16+ (for TypeScript)
- Git

### Android App Setup

1. **Clone and open project:**
```bash
   git clone https://github.com/SanthoshBR-30/EdgeVisionRT.git
   cd EdgeVisionRT
```
   Open in Android Studio and wait for Gradle sync.

2. **Install NDK and CMake:**
```
   Tools → SDK Manager → SDK Tools tab
   ☑ NDK (Side by side)
   ☑ CMake
   Click "Apply"
```

3. **Build native libraries:**
```bash
   ./gradlew clean
   ./gradlew :app:externalNativeBuildDebug
```

4. **Run application:**
   - Connect Android device (API 24+) or start emulator
   - Click Run (▶️) or `Shift + F10`
   - Grant camera permission when prompted
   - App starts in portrait mode with edge detection

### NDK Configuration

**CMakeLists.txt:**
```cmake
cmake_minimum_required(VERSION 3.22.1)
project("edgevisionrt")

add_library(edgevisionrt SHARED
        src/main/cpp/native-lib.cpp)

find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

target_link_libraries(edgevisionrt
        ${log-lib}
        ${jnigraphics-lib})
```

### Web Viewer Setup

1. **Navigate to web folder:**
```bash
   cd web
```

2. **Install TypeScript:**
```bash
   npm install
```

3. **Compile TypeScript:**
```bash
   npm run build
```
   Output: `dist/viewer.js`


4. **Open web viewer:**
```bash
   start index.html
   # Open in browser
   open web/index.html
   
```
---

### Architecture
Camera2 → Kotlin → JNI → C++ Sobel Edge Detection → OpenGL ES Renderer → Display on Android

TypeScript Viewer:
Static raw_frame.jpg + edge_frame.jpg → DOM → Stats Overlay

**Native C++ Flow**
- Receive RGBA bitmap  
- Convert to grayscale  
- Apply Sobel (Gx + Gy)  
- Output edges as white on black  

**OpenGL ES**
- Upload processed bitmap as texture  
- Render full-screen quad with shaders  


## 🔗 Repository

**GitHub:** -https://github.com/SanthoshBR-30/EdgeVisionRT







