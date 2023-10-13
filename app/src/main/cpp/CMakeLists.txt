cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_VERBOSE_MAKEFILE on)

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
        midi/MidiManager.cpp
        )

target_link_libraries(native_midi PRIVATE amidi OpenSLES android log)

add_library(fluidsynth SHARED
        fluidsynth/fluid_adriver.c fluidsynth/fluid_dll.c fluidsynth/fluid_list.c fluidsynth/fluid_seq.c
        fluidsynth/fluid_mdriver.c fluidsynth/fluid_seqbind.c fluidsynth/fluid_aufile.c fluidsynth/fluid_cmd.c
        fluidsynth/fluid_dsp_float.c fluidsynth/fluid_midi.c fluidsynth/fluid_settings.c
        fluidsynth/fluid_chan.c fluidsynth/fluid_midi_router.c fluidsynth/fluid_sndmgr.c
        fluidsynth/fluid_chorus.c fluidsynth/fluid_event.c fluidsynth/fluid_synth.c
        fluidsynth/fluid_gen.c fluidsynth/fluid_mod.c fluidsynth/fluid_sys.c
        fluidsynth/fluid_conv.c fluidsynth/fluid_hash.c fluidsynth/fluid_tuning.c
        fluidsynth/fluid_voice.c fluidsynth/fluid_io.c
        fluidsynth/fluid_dart.c fluidsynth/fluid_ramsfont.c
        fluidsynth/fluid_defsfont.c fluidsynth/fluid_rev.c
        fluidsynth/sf2_synth_engine.c)

target_include_directories(fluidsynth PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/fluidsynth/include)

target_compile_definitions(fluidsynth PRIVATE
        HAVE_PTHREAD_H
        HAVE_STDLIB_H
        HAVE_STDIO_H
        HAVE_MATH_H
        HAVE_STRING_H
        HAVE_STDARG_H
        HAVE_SYS_SOCKET_H
        HAVE_NETINET_IN_H
        HAVE_ARPA_INET_H
        HAVE_NETINET_TCP_H
        HAVE_UNISTD_H
        HAVE_ERRNO_H
        HAVE_FCNTL_H
        VERSION=1.0.9)

target_link_libraries(fluidsynth log)
