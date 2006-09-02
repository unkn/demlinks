bin_PROGRAMS = dml_imagine setup_inputs

noinst_HEADERS = 	notetrk.h	\
			pnotetrk.h	\
			_cppdefs.h	\
			_gcdefs.h	\
			dmlenv.h	\
			globaltimer.h \
			genericinput.h \
			timedinput.h \
			timedmouse.h \
			timedkeys.h   \
			input.h \
			buffer.h \
			actionsinput.h \
			actions.h \
			consts.h \
			camera.h \
			excamera.h \
			fps.h \
			init.h \
			square.h \
			activefunx.h \
			flags.h \
			DLL.h \
			SLL.h \
			actionsreplay.h \
			threadslock.h	\
			macros.h \
			portability.h

dml_imagine_SOURCES = 	notetrk.cpp	\
			pnotetrk.cpp	\
			dmlenv.cpp	\
			globaltimer.cpp \
			timedinput.cpp  \
			timedmouse.cpp \
			timedkeys.cpp \
			input.cpp \
			genericinput.cpp \
			actionsinput.cpp \
			actions.cpp \
			init.cpp \
			main.cpp	\
			camera.cpp \
			excamera.cpp \
			fps.cpp \
			square.cpp \
			flags.cpp \
			activefunx.cpp \
			actionsreplay.cpp \
			portability.cpp

setup_inputs_SOURCES = 	notetrk.cpp	\
			pnotetrk.cpp	\
			setup_inputs.cpp \
			dmlenv.cpp	\
			polishform.cpp \
			portability.cpp

INCLUDES = -I$(top_builddir)
#setup_inputs_LDFLAGS  = -ldb_cxx
#setup_inputs_CXXFLAGS = -pipe -Wall -Werror -Wfatal-errors
setup_inputs_CXXFLAGS = @DEVFLAGS@

#dml_imagine_LDFLAGS =  -ldb_cxx
##dml_imagine_LDFLAGS += `allegro-config --libs`
#dml_imagine_CXXFLAGS = -pipe -Wall -Werror -Wfatal-errors
dml_imagine_CXXFLAGS = @DEVFLAGS@




