import sys
import RFA

# parameters: parentDir, outputFile, compress (true/false), removeAllNonServerFiles (true/false)

rfa = RFA.RefractorFlatArchive("", read=False)
rfa.addDirectory(sys.argv[3], base_directory=sys.argv[1])
if sys.argv[5] == "true":
    rfa.deleteAllNonServerFiles()
rfa.write(sys.argv[2], sys.argv[4] == "true")
exit(0)
