cmake_minimum_required(VERSION 3.4.1)

# Pull in parselib
set(PARSELIB_DIR ../../../../oboe/samples/parselib)
#message("PARSELIB_DIR = " + ${PARSELIB_DIR})

# Pull in iolib
set(IOLIB_DIR ../../../../oboe/samples/iolib)
#message("IOLIB_DIR = " + ${IOLIB_DIR})

# Set the path to the Oboe library directory
set(OBOE_DIR ../../../../oboe)

add_subdirectory(${OBOE_DIR} ./oboe-bin)

include_directories(taskqueue)

# include folders
include_directories(
        ${OBOE_DIR}/include
        ${CMAKE_CURRENT_LIST_DIR}
)

include(${PARSELIB_DIR}/src/main/cpp/CMakeLists.txt)
include(${IOLIB_DIR}/src/main/cpp/CMakeLists.txt)

# Include the WavLib headers and shared sample code
include_directories(
        ${PARSELIB_DIR}/src/main/cpp
        ${IOLIB_DIR}/src/main/cpp)

# App specific sources
set(APP_SOURCES
        SoundEngine.cpp
        RecordingIO.cpp
        utils/Utils.h
        )

# Build the (native) library
add_library(soundengine SHARED
        ${APP_SOURCES}
        )

# Enable optimization flags: if having problems with source level debugging,
# disable -Ofast ( and debug ), re-enable after done debugging.
target_compile_options(soundengine PRIVATE -Wall -Werror "$<$<CONFIG:RELEASE>:-Ofast>")

target_link_libraries( # Specifies the target library.
        soundengine

        -Wl,--whole-archive
        iolib
        parselib
        -Wl,--no-whole-archive

        oboe

        # Links the target library to the log library
        # included in the NDK.
        log)

# add native midi
add_library(native_midi
        SHARED
        midi/AppMidiManager.cpp
        utils/midiUtil.cpp
        )

target_link_libraries(native_midi PRIVATE amidi OpenSLES android log)