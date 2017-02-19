####################################################################################################
### INITIALISATION PARAMS ##########################################################################
####################################################################################################

cmake_minimum_required(VERSION 3.4.1)
option(TEST "Enable compilation of tests" ON)

####################################################################################################
### GOOGLE TEST - SETUP ############################################################################
####################################################################################################

if (TEST)
    # Location of GoogleTest library
    set(GTEST_LOC ${PROJECT_SOURCE_DIR}/lib/googletest-release-1.7.0)

    # Include path for headers in the library
    include_directories(${GTEST_LOC} ${GTEST_LOC}/include ${GTEST_LOC}/include/gtest/internal)

    # Adding GoogleTest libraries - not expecting to recompile these again
    add_library(gtest_all               STATIC ${GTEST_LOC}/src/gtest-all.cc)
    add_library(gtest_main              STATIC ${GTEST_LOC}/src/gtest_main.cc)

    # Enable make test
    enable_testing()
endif()

####################################################################################################
#### COMPILATION DECLARATIONS - MAIN ###############################################################
####################################################################################################

include_directories(${PROJECT_SOURCE_DIR}/lib)


# User build libraries for compilation into .o
### PNGLib ###
add_library(GenUtils                STATIC lib/PNGLib/cpp/GenUtils.cpp)
add_library(PNGMDFrmtr              STATIC lib/PNGLib/cpp/PNGMetaDataFormatter.cpp)
add_library(PNGCmp                  STATIC lib/PNGLib/cpp/PNGComparison.cpp)
add_library(PNGStructs              STATIC lib/PNGLib/cpp/PNGStructs.cpp)

### Android Source ###
# User build libraries for compilation into .so
add_library(imgManipExample         SHARED src/imgManipExample/imgManipExample.cpp)

####################################################################################################
#### COMPILATION DECLARATIONS - TEST ###############################################################
####################################################################################################

if (TEST)
    add_library(ContiguousStreamTest    SHARED test/Collections/ContiguousStreamTest.cpp)
endif()

####################################################################################################
#### DEPENDENCY LINKAGE - MAIN #####################################################################
####################################################################################################

# Linking libraries to imgManipExample
target_link_libraries(imgManipExample
                      android
                      log
                      PNGStructs
                      PNGCmp
                      PNGMDFrmtr
                      GenUtils
                     )

####################################################################################################
#### DEPENDENCY LINKAGE - TEST #####################################################################
####################################################################################################

if (TEST)
    # Linking libraries to ContinuousStreamTest
    target_link_libraries(ContiguousStreamTest gtest_main gtest_all)
endif()

####################################################################################################
####################################################################################################