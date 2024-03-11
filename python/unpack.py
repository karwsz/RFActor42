import sys
import RFA

# parameters: rfaFile, outputDirectory

rfa = RFA.RefractorFlatArchive(sys.argv[1])
rfa.extractAll(destinationDir=sys.argv[2])
exit(0)
