# An Android.mk file must begin with the definition of the LOCAL_PATH variable.
# It is used to locate source files in the development tree.
# The macro function 'my-dir', provided by the build system, is used to return the path
# of the current directory (i.e. the directory containing the Android.mk file itself).

LOCAL_PATH := $(call my-dir)


# The CLEAR_VARS variable points to a special GNU Makefile that clears many LOCAL_XXX variables
# for you, such as LOCAL_MODULE, LOCAL_SRC_FILES, and LOCAL_STATIC_LIBRARIES.
# Note that it does not clear LOCAL_PATH. This variable must retain its value because the
# system parses all build control files in a single GNU Make execution context where all variables
# are global. You must (re-)declare this variable before describing each module.

include $(CLEAR_VARS)


# The LOCAL_MODULE variable stores the name of the module that you wish to build.
# Use this variable once per module in your application.
# Each module name must be unique and not contain any spaces.
# The build system, when it generates the final shared-library file, automatically adds the proper
# prefix and suffix to the name that you assign to LOCAL_MODULE. For example, this results
# in generation of a library called libnative-lib.so.

LOCAL_MODULE    		:= native-lib

# The next line enumerates the source files, with spaces delimiting multiple files. The
# LOCAL_SRC_FILES variable must contain a list of C and/or C++ source files to build into a module.

LOCAL_SRC_FILES 		:= native-lib.cpp


# Applies optimization flags like O2

APP_OPTIM 				:= release


# The last line helps the system tie everything together.
# The BUILD_SHARED_LIBRARY variable points to a GNU Makefile script that collects all the
# information you defined in LOCAL_XXX variables since the most recent include. This script
# determines what to build, and how to do it.

include $(BUILD_SHARED_LIBRARY)


# The BUILD_EXECUTABLE is needed to build code and execute it as command line (es. ls -a)
# include $(BUILD_EXECUTABLE)