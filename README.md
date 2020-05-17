# SportHighlightsAutoEditor
The Sport Highlights Auto Editor is an optimization tool that simplifies video editing
to create a highlight reel. This tool utilizes Google Cloud's Video Intelligence API 
to identify important segments of a Sports broadcast recording and compiles them for 
your entertainment. 

## Installation

The user requires [JDK 11.0.7](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) and [JavaFX 11.0.2](https://gluonhq.com/products/javafx/)
to be set up on their system. 

## Usage

1. Launch the project on an IDE such as IntelliJ
2. Ensure JavaFX is added to the libraries from the project structure
3. Ensure that the Lombok plugin is installed
4. Go to Run and Edit Configuration on IntelliJ to add the following
to the VM Options
5. The user should be able to build this with maven and run the application
6. The application requires the user to select a local MP4 football (soccer) video 
and set the output directory for the highlights file
7. Upon completion of the generation process, the user can watch the clip from the
output directory
 

```
--module-path {file path to JavaFX lib} --add-modules javafx.controls,javafx.fxml
--add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED
 ```

## Contributors

We have three contributors for this project.

1. Tawsif Hasan (https://github.com/T4w51f) who primarily worked on parsing Google Cloud's video annotation JSON and providing us a list of time frames to use
2. Amr Almoallim (https://github.com/amrm3lm) who worked on trimming video according to the time frames provided and merging videos to generate highlights
3. Ahnaf Ahsan (https://github.com/Ahnaf39) who worked on the UI of application and it interacting with the file explorer of the opersating system

## License
[MIT](https://choosealicense.com/licenses/mit/)