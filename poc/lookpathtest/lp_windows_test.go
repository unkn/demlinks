package exec_test

import (
	// "fmt"
	"io/ioutil"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"testing"
)

const PATHvar = "PATH"
const PATH_separator = os.PathListSeparator
const PATHEXT_separator = ";"
const PATHEXTvar = "PATHEXT"
const comspecvar = "COMSPEC"

func TestLookPath1(t *testing.T) {
	println("testing...")
	//var myt TMine = TMine(*t)
	myt := (*TMine)(t)

	tmp, err := ioutil.TempDir("", "testWindowsLookPathTests")
	if err != nil {
		t.Fatalf("TempDir failed: %v", err)
	}
	defer os.RemoveAll(tmp)

	// myt.setEnv("COMSpec", "cmd.exe")
	comspec := os.Getenv(comspecvar)
	if comspec == "" {
		t.Fatalf("%s must be set", comspecvar)
	}

	dir, file := filepath.Split(comspec)
	// println(file)
	if dir == "" {
		t.Fatalf("Expected to have a path in %%%s%% 's value: `%s`", comspecvar, comspec)
	}

	//TODO: make one char be lowercase or uppercase in the file to look for
	paths := []string{
		dir,
		os.Getenv(PATHvar),
		tmp,
		".",
		"",
	}

	t.testFor(file, dir+file, paths)

}

//adding extra "methods" to testing.T to avoid having to pass it as an arg
type TMine testing.T

/*func (t *TMine) testFor(lookfor, expected string, envvar, envvalue string, list []string, fun func()) {
	accval := ""
	for _, val := range list {
		fun(val, lookfor, expected)
		if !testing.Short() {
			accval += PATH_separator + val
			fun(accval, lookfor, expected)
		}
	}
}*/

func (t *TMine) testFor(lookfor, expected string, paths []string) {
	accval := ""
	for _, val := range paths {
		t.testWithPATH(val, lookfor, expected)
		if !testing.Short() {
			accval += PATH_separator + val
			t.testWithPATH(accval, lookfor, expected)
		}
	}
}

func (t *TMine) testWithPATH(path, lookfor, expected string) {
	t.setEnv(PATHvar, path)

	pathexts := []string{
		".EXE",
		".BAT",
		".",
		"",
	}

	accval := ""
	for _, val := range pathexts {
		t.testWithPATHEXT(val, lookfor, expected)
		if !t.Short() {
			accval += PATHEXT_separator + val
			t.testWithPATHEXT(accval, lookfor, expected)
		}
	}
}

func (t *TMine) testWithPATHEXT(pathext, lookfor, expected string) {
	t.setEnv(PATHEXTvar, pathext)
	t.expectFind(lookfor, expected)
}

func (t *TMine) expectFind(lookfor, expected string) {
	found, err := exec.LookPath(lookfor)
	if err != nil {
		t.Errorf("Failed to find `%s`, expected to find it as `%s` but got an error instead: `%s`", lookfor, expected, err)
		return
	}

	if (found != expected) && (strings.ToUpper(found) != strings.ToUpper(expected)) {
		t.Errorf("Failed to find `%s`, expected to find it as `%s` but got `%s` instead.", lookfor, expected, found)
		return
	}

	t.ensureExitsAsFile(found)
}

//as file or symlink to file, even if symlink is broken
func (t *TMine) ensureExitsAsFile(fname string) {
	finfo, err := os.Stat(fname)
	if err != nil {
		t.Errorf("Expected `%s` to exist, error: `%s`", fname, err)
		return
	}
	if !finfo.IsDir() {
		//could be symlink file
		//TODO: test if searched file is symlinkd or symlinkfile or dir or normal file, also readonly file, sysfile, hidden file + dirs
		//also when symlinkfile is broken (points to nothing)
		return
	}
	t.Errorf("Expected `%s` to exist as a file, but it's got unexpected mode `%s`", fname, finfo.Mode())
}

func (t *TMine) setEnv(key, value string) {
	if err := os.Setenv(key, value); err != nil {
		t.Fatalf(err)
	}

	if now_val := os.Getenv(key); value != now_val {
		t.Fatalf("Couldn't set `%s` env var, \ncurrent value: \n`%s`, \nwanted value: \n`%s`", key, now_val, value)
	}
}
