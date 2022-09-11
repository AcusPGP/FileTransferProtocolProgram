package view.filetransferprotocolprogram;

public class ListFile {
    private int number;
    private String fileName;

    public ListFile(int number, String fileName) {
        this.number = number;
        this.fileName = fileName;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
