bin_PROGRAMS = dml_imagine definekeys

noinst_HEADERS = 	notetrk.h	\
			pnotetrk.h	\
			_cppdefs.h	\
			_gcdefs.h	\
			genericinput.h \
			timedinput.h \
			timedmouse.h \
			timedkeys.h   \
			timedmouse.cpp \
			timedkeys.cpp \
			input.h \
			common.h \
			consts.h \
			camera.h \
			excamera.h \
			fps.h \
			init.h \
			square.h \
			draw_text.h \
			combifile.h \
			activefunx.h \
			actionsreplay.h

dml_imagine_SOURCES = 	notetrk.cpp	\
			pnotetrk.cpp	\
			main.cpp	\
			genericinput.cpp \
			timedinput.cpp  \
			input.cpp \
			camera.cpp \
			excamera.cpp \
			fps.cpp \
			init.cpp \
			square.cpp \
			draw_text.cpp \
			combifile.cpp \
			activefunx.cpp \
			actionsreplay.cpp

dml_imagine_LDFLAGS = `allegro-config --libs`
dml_imagine_CXXFLAGS = `allegro-config --cppflags` -pipe -Wall -Werror

definekeys_SOURCES = gi.cpp \
		     genericinput.cpp \
		     combifile.cpp \
		     pnotetrk.cpp \
		     timedinput.cpp \
		     notetrk.cpp \
			actionsreplay.cpp \
		     input.cpp

definekeys_LDFLAGS = `allegro-config --libs`
definekeys_CXXFLAGS = `allegro-config --cppflags` -pipe -Wall -Werror

