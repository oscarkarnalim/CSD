from yapf.yapflib.yapf_api import FormatFile
import sys
# using yapf library taken from https://github.com/google/yapf/
# version v0.28.0

# take the source path
sourcePath = sys.argv[1]
# take the first argument as the filepath
# then update the targeted code with their format corrected.
# this function returns an array consisting the formatted code, type, and a boolean.
# that boolean is assigned to true if the function works.
result = FormatFile(sourcePath, in_place=True, style_config=sys.argv[2])

# this program will display error output once the code is not parseable.
