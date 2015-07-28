#ifndef __glwindow_h__
#define __glwindow_h__

typedef enum
{
  /* Key codes match the SDL ones  */
  GLWK_UNKNOWN = 0,
  GLWK_FIRST = 0,

        GLWK_BACKSPACE  = 8,
        GLWK_TAB                = 9,
        GLWK_CLEAR              = 12,
        GLWK_RETURN             = 13,
        GLWK_PAUSE              = 19,
        GLWK_ESCAPE             = 27,
        GLWK_SPACE              = 32,
        GLWK_EXCLAIM    = 33,
        GLWK_QUOTEDBL   = 34,
        GLWK_HASH               = 35,
        GLWK_DOLLAR             = 36,
        GLWK_AMPERSAND  = 38,
        GLWK_QUOTE              = 39,
        GLWK_LEFTPAREN  = 40,
        GLWK_RIGHTPAREN = 41,
        GLWK_ASTERISK   = 42,
        GLWK_PLUS               = 43,
        GLWK_COMMA              = 44,
        GLWK_MINUS              = 45,
        GLWK_PERIOD             = 46,
        GLWK_SLASH              = 47,
        GLWK_0              = 48,
        GLWK_1              = 49,
        GLWK_2          = 50,
        GLWK_3          = 51,
        GLWK_4          = 52,
        GLWK_5          = 53,
        GLWK_6          = 54,
        GLWK_7          = 55,
        GLWK_8          = 56,
        GLWK_9          = 57,
        GLWK_COLON              = 58,
        GLWK_SEMICOLON  = 59,
        GLWK_LESS               = 60,
        GLWK_EQUALS             = 61,
        GLWK_GREATER    = 62,
        GLWK_QUESTION   = 63,
        GLWK_AT         = 64,
        /*
           Skip uppercase letters
         */
        GLWK_LEFTBRACKET        = 91,
        GLWK_BACKSLASH  = 92,
        GLWK_RIGHTBRACKET       = 93,
        GLWK_CARET              = 94,
        GLWK_UNDERSCORE = 95,
        GLWK_BACKQUOTE  = 96,
        GLWK_a          = 97,
        GLWK_b          = 98,
        GLWK_c          = 99,
        GLWK_d          = 100,
        GLWK_e          = 101,
        GLWK_f          = 102,
        GLWK_g          = 103,
        GLWK_h          = 104,
        GLWK_i          = 105,
        GLWK_j          = 106,
        GLWK_k          = 107,
        GLWK_l          = 108,
        GLWK_m          = 109,
        GLWK_n          = 110,
        GLWK_o          = 111,
        GLWK_p          = 112,
        GLWK_q          = 113,
        GLWK_r          = 114,
        GLWK_s          = 115,
        GLWK_t          = 116,
        GLWK_u          = 117,
        GLWK_v          = 118,
        GLWK_w          = 119,
        GLWK_x          = 120,
        GLWK_y          = 121,
        GLWK_z          = 122,
        GLWK_DELETE             = 127,
        /* End of ASCII mapped keysyms */

        /* International keyboard syms */
        GLWK_WORLD_0    = 160,          /* 0xA0 */
        GLWK_WORLD_1    = 161,
        GLWK_WORLD_2    = 162,
        GLWK_WORLD_3    = 163,
        GLWK_WORLD_4    = 164,
        GLWK_WORLD_5    = 165,
        GLWK_WORLD_6    = 166,
        GLWK_WORLD_7    = 167,
        GLWK_WORLD_8    = 168,
        GLWK_WORLD_9    = 169,
        GLWK_WORLD_10   = 170,
        GLWK_WORLD_11   = 171,
        GLWK_WORLD_12   = 172,
        GLWK_WORLD_13   = 173,
        GLWK_WORLD_14   = 174,
        GLWK_WORLD_15   = 175,
        GLWK_WORLD_16   = 176,
        GLWK_WORLD_17   = 177,
        GLWK_WORLD_18   = 178,
        GLWK_WORLD_19   = 179,
        GLWK_WORLD_20   = 180,
        GLWK_WORLD_21   = 181,
        GLWK_WORLD_22   = 182,
        GLWK_WORLD_23   = 183,
        GLWK_WORLD_24   = 184,
        GLWK_WORLD_25   = 185,
        GLWK_WORLD_26   = 186,
        GLWK_WORLD_27   = 187,
        GLWK_WORLD_28   = 188,
        GLWK_WORLD_29   = 189,
        GLWK_WORLD_30   = 190,
        GLWK_WORLD_31   = 191,
        GLWK_WORLD_32   = 192,
        GLWK_WORLD_33   = 193,
        GLWK_WORLD_34   = 194,
        GLWK_WORLD_35   = 195,
        GLWK_WORLD_36   = 196,
        GLWK_WORLD_37   = 197,
        GLWK_WORLD_38   = 198,
        GLWK_WORLD_39   = 199,
        GLWK_WORLD_40   = 200,
        GLWK_WORLD_41   = 201,
        GLWK_WORLD_42   = 202,
        GLWK_WORLD_43   = 203,
        GLWK_WORLD_44   = 204,
        GLWK_WORLD_45   = 205,
        GLWK_WORLD_46   = 206,
        GLWK_WORLD_47   = 207,
        GLWK_WORLD_48   = 208,
        GLWK_WORLD_49   = 209,
        GLWK_WORLD_50   = 210,
        GLWK_WORLD_51   = 211,
        GLWK_WORLD_52   = 212,
        GLWK_WORLD_53   = 213,
        GLWK_WORLD_54   = 214,
        GLWK_WORLD_55   = 215,
        GLWK_WORLD_56   = 216,
        GLWK_WORLD_57   = 217,
        GLWK_WORLD_58   = 218,
        GLWK_WORLD_59   = 219,
        GLWK_WORLD_60   = 220,
        GLWK_WORLD_61   = 221,
        GLWK_WORLD_62   = 222,
        GLWK_WORLD_63   = 223,
        GLWK_WORLD_64   = 224,
        GLWK_WORLD_65   = 225,
        GLWK_WORLD_66   = 226,
        GLWK_WORLD_67   = 227,
        GLWK_WORLD_68   = 228,
        GLWK_WORLD_69   = 229,
        GLWK_WORLD_70   = 230,
        GLWK_WORLD_71   = 231,
        GLWK_WORLD_72   = 232,
        GLWK_WORLD_73   = 233,
        GLWK_WORLD_74   = 234,
        GLWK_WORLD_75   = 235,
        GLWK_WORLD_76   = 236,
        GLWK_WORLD_77   = 237,
        GLWK_WORLD_78   = 238,
        GLWK_WORLD_79   = 239,
        GLWK_WORLD_80   = 240,
        GLWK_WORLD_81   = 241,
        GLWK_WORLD_82   = 242,
        GLWK_WORLD_83   = 243,
        GLWK_WORLD_84   = 244,
        GLWK_WORLD_85   = 245,
        GLWK_WORLD_86   = 246,
        GLWK_WORLD_87   = 247,
        GLWK_WORLD_88   = 248,
        GLWK_WORLD_89   = 249,
        GLWK_WORLD_90   = 250,
        GLWK_WORLD_91   = 251,
        GLWK_WORLD_92   = 252,
        GLWK_WORLD_93   = 253,
        GLWK_WORLD_94   = 254,
        GLWK_WORLD_95   = 255,          /* 0xFF */

        /* Numeric keypad */
        GLWK_KP0                = 256,
        GLWK_KP1                = 257,
        GLWK_KP2                = 258,
        GLWK_KP3                = 259,
        GLWK_KP4                = 260,
        GLWK_KP5                = 261,
        GLWK_KP6                = 262,
        GLWK_KP7                = 263,
        GLWK_KP8                = 264,
        GLWK_KP9                = 265,
        GLWK_KP_PERIOD  = 266,
        GLWK_KP_DIVIDE  = 267,
        GLWK_KP_MULTIPLY        = 268,
        GLWK_KP_MINUS   = 269,
        GLWK_KP_PLUS    = 270,
        GLWK_KP_ENTER   = 271,
        GLWK_KP_EQUALS  = 272,

        /* Arrows + Home/End pad */
        GLWK_UP         = 273,
        GLWK_DOWN               = 274,
        GLWK_RIGHT              = 275,
        GLWK_LEFT               = 276,
        GLWK_INSERT             = 277,
        GLWK_HOME               = 278,
        GLWK_END                = 279,
        GLWK_PAGEUP             = 280,
        GLWK_PAGEDOWN   = 281,

  /* Function keys */
  GLWK_F1         = 282,
  GLWK_F2         = 283,
  GLWK_F3         = 284,
  GLWK_F4         = 285,
  GLWK_F5         = 286,
  GLWK_F6         = 287,
  GLWK_F7               = 288,
  GLWK_F8               = 289,
  GLWK_F9               = 290,
  GLWK_F10              = 291,
  GLWK_F11              = 292,
  GLWK_F12              = 293,
  GLWK_F13              = 294,
  GLWK_F14              = 295,
  GLWK_F15              = 296,

        /* Key state modifier keys */
  GLWK_NUMLOCK  = 300,
  GLWK_CAPSLOCK = 301,
  GLWK_SCROLLOCK        = 302,
  GLWK_RSHIFT           = 303,
  GLWK_LSHIFT           = 304,
  GLWK_RCTRL            = 305,
  GLWK_LCTRL            = 306,
  GLWK_RALT             = 307,
  GLWK_LALT             = 308,
  GLWK_RMETA            = 309,
  GLWK_LMETA            = 310,
  GLWK_LSUPER           = 311,          /* Left "Windows" key */
  GLWK_RSUPER           = 312,          /* Right "Windows" key */
  GLWK_MODE             = 313,          /* "Alt Gr" key */
  GLWK_COMPOSE  = 314,          /* Multi-key compose key */

  /* Miscellaneous function keys */
  GLWK_HELP     =            315,
  GLWK_PRINT =           316,
  GLWK_SYSREQ =          317,
  GLWK_BREAK =           318,
  GLWK_MENU     =            319,
  GLWK_POWER =           320,           /* Power Macintosh power key */
  GLWK_EURO =            321,           /* Some european keyboards */
  GLWK_UNDO     =            322,               /* Atari keyboard has Undo */

  /* Mouse buttons (max 8 buttons) */
  GLWK_MOUSE_BUTTON1 = 323,
  GLWK_LBUTTON = 323,
  GLWK_MOUSE_BUTTON2 = 324,
  GLWK_MBUTTON = 324,
  GLWK_MOUSE_BUTTON3 = 325,
  GLWK_RBUTTON = 325,
  GLWK_MOUSE_BUTTON4 = 326,
  GLWK_MOUSE_BUTTON5 = 327,
  GLWK_MOUSE_BUTTON6 = 328,
  GLWK_MOUSE_BUTTON7 = 329,
  GLWK_MOUSE_BUTTON8 = 330,

  GLWK_LAST
} GLWKey;




class GLWINDOW
{
  private:
    int width;
    int height;
    int bpp;
    bool done;
    bool active;
    float fps;

    bool key[GLWK_LAST];
    int mousePos[2];
    SDL_Surface *sdlSurface;
    int flags;

    void (*logicCallback)(GLWINDOW *gw, float frameTime, void *userData);
    void (*renderCallback)(GLWINDOW *gw, void *userData);
    void (*mouseMoveCallback)(GLWINDOW *gw, void *userData);
    void (*keyDownCallback)(GLWINDOW *gw, GLWKey key, char ascii, void *userData);
    void (*keyUpCallback)(GLWINDOW *gw, GLWKey key, void *userData);
    void (*resizeCallback)(GLWINDOW *gw, void *userData);
    void *userData;
    GLWINDOW();
  public:
    ~GLWINDOW();
    static GLWINDOW *create(const char *title, int w, int h, bool fs, bool rs, bool stencil);

    void mainLoop(void);
    void exitMainLoop(void);

    void setLogicCallback(void (*logicCallback)(GLWINDOW *gw, float frameTime, void *userData));
    void setRenderCallback(void (*renderCallback)(GLWINDOW *gw, void *userData));
    void setKeyDownCallback(void (*keyDownCallback)(GLWINDOW *gw, GLWKey key, char ascii, void *userData));
    void setKeyUpCallback(void (*keyUpCallback)(GLWINDOW *gw, GLWKey key, void *userData));
    void setMouseMoveCallback(void (*mouseMoveCallback)(GLWINDOW *gw, void *userData));
    void setResizeCallback(void (*resizeCallback)(GLWINDOW *gw, void *userData));
    void setCursorVisibility(bool visible);
    void setUserData(void *userData);

    bool getKeyState(GLWKey key) const;
    int getWidth(void) const;
    int getHeight(void) const;
    void getMousePositionUpper(int mpos[2]) const;
    void getMousePositionLower(int mpos[2]) const;
    int getMouseX(void) const;
    int getMouseYLower(void) const;
    int getMouseYUpper(void) const;
    int getCursorVisibility(void) const;
    void *getUserData(void);
    void getMouseRay(const float viewMatrix[16],
                     const float projMatrix[16],
                     float position[3], float direction[3]) const;

    float getFps(void) const;


};

#endif
