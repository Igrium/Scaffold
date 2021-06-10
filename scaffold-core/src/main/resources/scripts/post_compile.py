import inspect
from os import error
import sys
import importlib
from pathlib import Path
filename = sys.argv[1]
modulename = inspect.getmodulename(filename)

sys.path.append(str(Path(filename).parent.absolute()))
module = importlib.import_module(modulename)

if module is None:
    print("Unable to find script: "+filename)
    exit(1)

if getattr(module, 'post_compile', None) is not None:
    module.post_compile(sys.argv[2], sys.argv[3], sys.argv[4])