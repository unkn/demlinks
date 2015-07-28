package demlinks

import "testing"
import "os"
import "os/exec"

func setup(t *testing.T, extensions string) {
	setEnv(t, "PATHEXT", extensions)
	setEnv(t, "PATH", "C:\\windows\\system32")
}

func findWhere(t *testing.T) {
	lookPath(t, "where")
	lookPath(t, "where.exe")
	lookPath(t, "c:\\windows\\system32\\where.exe")
}

func TestEXE(t *testing.T) { //all works
	setup(t, ".EXE")
	findWhere(t)
}

func TestBAT(t *testing.T) { //all fail
	setup(t, ".BAT")
	findWhere(t)
}

func setEnv(t *testing.T, key, value string) {
	os.Setenv(key, value)
	if now_val := os.Getenv(key); value != now_val {
		t.Errorf("Couldn't set `%s` env var, current value `%s`, wanted value `%s`", key, now_val, value)
	}
}

func lookPath(t *testing.T, file string) {
	if ffile, err := exec.LookPath(file); err != nil {
		t.Error(err)
	} else {
		println("Found: ", ffile)
	}
}
