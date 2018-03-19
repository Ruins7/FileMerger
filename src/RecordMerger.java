public class RecordMerger {

	public static final String FILENAME_COMBINED = "combined";
    public static final String FILENAME_COMBINED_TYPE = ".csv";
    public static final String FILENAME_COMBINED_PATH = "./out";
    public static final String CSV = ".csv";
    public static final String HTML = ".html";
    // add new input file type here
    public static final String FILENAMEERROR = "Please enter legal file name (Wrong name: ";

	/**
	 * Entry point of this test.
	 *
	 * @param args command line arguments: first.html and second.csv.
	 * @throws Exception bad things had happened.
	 */
	public static void main(final String[] args) throws Exception {

		if (args.length == 0) {
			System.err.println("Please enter file names, try again");
			System.exit(1);
		}

        OperateFileContent ofc = new OperateFileContent();
		// multiple input files (new type input file needs to be add here)
		for(int i = 0; i < args.length; i++){
            //check if current file is a legal type
            String currentFile = args[i]+"";
            if(currentFile.endsWith(CSV)){
                ofc.exactCVSFile(currentFile);
            }else if(currentFile.endsWith(HTML)){
                ofc.exactHTMLFile(currentFile);
            }else{
                System.err.println(FILENAMEERROR + currentFile + ")");
            }
		}
        // generate final output file
        ofc.generateFinalResultFile(FILENAME_COMBINED, FILENAME_COMBINED_TYPE, FILENAME_COMBINED_PATH); 
	}
}
