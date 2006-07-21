bin_PROGRAMS = dml_imagine setup_inputs

noinst_HEADERS = 	notetrk.h	\
			pnotetrk.h	\
			_cppdefs.h	\
			_gcdefs.h	\
			classhit.h	\
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
			combifile.h \
			activefunx.h \
			flags.h \
			DLL.h \
			SLL.h \
			actionsreplay.h \
			threadslock.h	\
			macros.h

dml_imagine_SOURCES = 	notetrk.cpp	\
			pnotetrk.cpp	\
			classhit.cpp	\
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
			combifile.cpp \
			flags.cpp \
			activefunx.cpp \
			actionsreplay.cpp

setup_inputs_SOURCES = 	notetrk.cpp	\
			pnotetrk.cpp	\
			setup_inputs.cpp \
			classhit.cpp	\
			polishform.cpp	

setup_inputs_LDFLAGS = -L/usr/local/BerkeleyDB.4.4/lib -ldb_cxx `allegro-config --libs`
setup_inputs_CXXFLAGS = `allegro-config --cppflags` -pipe -Wall -Werror -Wfatal-errors -I/usr/local/BerkeleyDB.4.4/include/

dml_imagine_LDFLAGS = -L/usr/local/BerkeleyDB.4.4/lib -ldb_cxx
dml_imagine_LDFLAGS += `allegro-config --libs`
dml_imagine_CXXFLAGS = `allegro-config --cppflags` -pipe -Wall -Werror -Wfatal-errors -I/usr/local/BerkeleyDB.4.4/include/




