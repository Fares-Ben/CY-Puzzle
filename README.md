# 🧩 CY-PUZZLE

**CY-PUZZLE** is a Java application that solves real jigsaw puzzles by analyzing the shape and color of each piece and assembling them automatically. It includes a graphical interface built with **JavaFX** and a complete backend puzzle resolution pipeline based on visual analysis.

---

## 🚀 Features

- 🔍 Automatic detection of puzzle pieces from a folder of PNG images  
- 🧠 Edge detection with physical and visual compatibility analysis  
- 🤝 Matching based on shape (tenon/mortise), RGB color distribution, and edge profiles  
- 🧩 Puzzle solving without piece rotation  
- 🖼️ Final reconstruction and image assembly  
- 📈 Real-time JavaFX GUI to select pieces, run solver, and download the final result  

---

## 🛠️ How It Works

### 1. Image Analysis  
Each piece is processed using `PuzzleAnalyzer`, which:
- Extracts its shape using alpha transparency masks  
- Traces the contour and detects edges  
- Computes `EdgeResult` data per side:  
  - Shape type (tenon/mortise/flat)  
  - Segment lengths  
  - RGB averages  
  - Depth of concavity/convexity  
  - Pixel profile vector  

### 2. Edge Compatibility  
`EdgeCompatibilityChecker` compares two edges:
- Only tenon/mortise pairs are allowed  
- Length and color differences must remain under strict tolerances  
- Pixel profiles are matched to ensure proper shape fitting  

### 3. Puzzle Resolution  
`PuzzleSolver`:
- Finds the top-left corner (TOP & LEFT flat)  
- Builds the top row and left column using compatible pieces  
- Fills the grid by matching top and left neighbors  
- Outputs a matrix of piece IDs and a list of unplaced pieces  

### 4. Image Assembly  
`PuzzleImageViewer` reconstructs the final puzzle image by:
- Reading the placement matrix  
- Using the original piece images  
- Computing position offsets from piece corner coordinates  
- Assembling a final `BufferedImage`  

---

## 🧑‍💻 Getting Started

### ✅ Prerequisites
- Java 11 or higher  
- JavaFX SDK (openjfx)  
- JDK must be configured with JavaFX modules  

---

## 🔧 Dependencies

- `javafx.controls`  
- `java.awt` for `BufferedImage` & Swing (used in image viewer)  
- No external libraries – pure Java  

---

## 📈 Possible Improvements

- 🔄 Support for piece rotation  


---


### 📦 Run the project

```bash
# Compile (assuming JavaFX libs are in /path/to/javafx-sdk/lib)
javac --module-path "H:\Documents\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml,javafx.swing -d out src/CY_PUZZLE/*.java src/Factory/*.java src/Model/*.java src/Resolution_Puzzle/*.java

# Run
java --module-path "H:\Documents\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml,javafx.swing -cp out CY_PUZZLE.Main

java -cp out CY_PUZZLE.TerminalInterface

 
