package audio.samples.part1;

import audio.domain.WavFile;

public class WavFormatTest {
    public static void main(String[] args) {
        readAndRewriteWav("samples/sample");
    }

    private static void readAndRewriteWav(String filePathToReadFromMinusExtension) {
        String fileExtension = ".wav";
        String filePathToReadFrom = filePathToReadFromMinusExtension + fileExtension;
        String filePathToWriteTo = filePathToReadFromMinusExtension + "-New" + fileExtension;

        WavFile wavFileToTest = WavFile.readFromFilePath(filePathToReadFrom);
        wavFileToTest.filePath = filePathToWriteTo;
        wavFileToTest.writeToFilePath();
    }
}

