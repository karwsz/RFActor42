import sys
import RFA

# parameters: parentDir, outputFile, compress (true/false)

rfa = RFA.RefractorFlatArchive("", read=False)
rfa.addDirectory(sys.argv[1])
rfa.write(sys.argv[2], sys.argv[3] == "true")
exit(0)
