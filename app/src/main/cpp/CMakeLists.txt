cmake_minimum_required(VERSION 3.4.1)

# Pull in parselib
set(PARSELIB_DIR ${CMAKE_CURRENT_SOURCE_DIR}/parselib)

# Pull in iolib
set(IOLIB_DIR ${CMAKE_CURRENT_SOURCE_DIR}/iolib)

set(FLUID_SYNTH_LIB_DIR ${CMAKE_CURRENT_SOURCE_DIR}/fluidsynth/libs/${ANDROID_ABI})
set(FLUID_SYNTH_LIB_FILES
        libfluidsynth.so
        libglib-2.0.so
        libgthread-2.0.so
        libsndfile.so
        libinstpatch-1.0.so
        libgobject-2.0.so
        libpcre.so
        libvorbisenc.so
        libFLAC.so
        libopus.so
        libvorbis.so
        libogg.so
        )

set(LIB_DIR ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})
set(LIB_FILES
        libomp.so
        )

include_directories(taskqueue)

# include folders
include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}/fluidsynth/include
        ${CMAKE_CURRENT_SOURCE_DIR}/oboe/include
        ${CMAKE_CURRENT_LIST_DIR}
)

include(${PARSELIB_DIR}/src/main/cpp/CMakeLists.txt)
include(${IOLIB_DIR}/src/main/cpp/CMakeLists.txt)

include_directories(
        ${PARSELIB_DIR}/src/main/cpp
        ${IOLIB_DIR}/src/main/cpp)

# App specific sources
set(APP_SOURCES
        SoundEngine.cpp
        RecordingIO.cpp
        )

# Build the (native) library
add_library(soundengine SHARED
        ${APP_SOURCES}
        )

foreach (FLUID_SYNTH_LIB_FILE ${FLUID_SYNTH_LIB_FILES})
    add_library(${FLUID_SYNTH_LIB_FILE} SHARED IMPORTED)
    set_target_properties(${FLUID_SYNTH_LIB_FILE} PROPERTIES
            IMPORTED_LOCATION "${FLUID_SYNTH_LIB_DIR}/${FLUID_SYNTH_LIB_FILE}"
            )
    target_link_libraries(soundengine ${FLUID_SYNTH_LIB_FILE})
endforeach ()

foreach (LIB_FILE ${LIB_FILES})
    add_library(${LIB_FILE} SHARED IMPORTED)
    set_target_properties(${LIB_FILE} PROPERTIES
            IMPORTED_LOCATION "${LIB_DIR}/${LIB_FILE}"
            )
    target_link_libraries(soundengine ${LIB_FILE})
endforeach ()

add_library(oboe SHARED IMPORTED)
set_target_properties(oboe PROPERTIES
        IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/oboe/libs/android.${ANDROID_ABI}/liboboe.so"
        )

target_link_libraries( # Specifies the target library.
        soundengine

        -Wl,--whole-archive
        -Wl,--hash-style=both
        iolib
        parselib
        -Wl,--no-whole-archive
        -Wl,--hash-style=both

        oboe

        # Links the target library to the log library
        # included in the NDK.
        log)

# add native midi
add_library(midi
        SHARED
        midi/MidiManager.cpp
        )

target_link_libraries(midi PRIVATE amidi OpenSLES android log)
