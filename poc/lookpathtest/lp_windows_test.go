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
const PATH_separator = string(os.PathListSeparator)
const folders_separator = string(os.PathSeparator)
const PATHEXT_separator = ";"
const PATHEXTvar = "PATHEXT"
const comspecvar = "COMSPEC"

func TestLookPath1(t *testing.T) {
	println("testing...")
	//var myt TMine = TMine(*t)
	myt := (*TMine)(t)

	tmp, err1 := ioutil.TempDir("", "testWindowsLookPathTests")
	if err1 != nil {
		myt.Fatalf("TempDir failed: %v", err1)
	}
	defer os.RemoveAll(tmp)

	// myt.setEnv("COMSpec", "cmd.exe")
	comspec := os.Getenv(comspecvar)
	if comspec == "" {
		myt.Fatalf("%s must be set", comspecvar)
	}

	dir, file := filepath.Split(comspec)
	// println(file)
	if dir == "" {
		myt.Fatalf("Expected to have a path in %%%s%% 's value: `%s`", comspecvar, comspec)
	}

	//TODO: paths in PATH with ending and without ending "\\"
	//TODO: make one char be lowercase or uppercase in the file to look for
	paths := []string{
		dir,
		os.Getenv(PATHvar),
		tmp,
		".",
		"",
	}

	//should the entire test fail ie. FailNow() when one of them fails? hmm, maybe it's best that way

	if err := myt.testFor(file, dir+file, paths); err != nil {
		myt.Error(err)
	} else {
		balddir := strings.TrimSuffix(dir, folders_separator)
		myt.testFor(file, balddir+file, paths)
	}

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

func (t *TMine) MyErrorf(format string, args ...interface{}) []interface{} {
	t.Errorf(format, args...)
	return args
}

func (t *TMine) testFor(lookfor, expected string, paths []string) []interface{} {
	accval := ""
	for _, val := range paths {
		if err := t.testWithPATH(val, lookfor, expected); err != nil {
			return err
		}
		if !testing.Short() {
			accval += PATH_separator + val
			if err := t.testWithPATH(accval, lookfor, expected); err != nil {
				return err
			}
		}
	}

	return nil
}

func (t *TMine) testWithPATH(path, lookfor, expected string) []interface{} {
	t.setEnv(PATHvar, path)

	pathexts := []string{
		".EXE",
		".BAT",
		".",
		"",
	}

	accval := ""
	for _, val := range pathexts {
		if err := t.testWithPATHEXT(val, lookfor, expected); err != nil {
			return err
		}
		if !testing.Short() {
			accval += PATHEXT_separator + val
			if err := t.testWithPATHEXT(accval, lookfor, expected); err != nil {
				return err
			}
		}
	}

	return nil
}

func (t *TMine) testWithPATHEXT(pathext, lookfor, expected string) []interface{} {
	t.setEnv(PATHEXTvar, pathext)
	return t.expectFind(lookfor, expected)
}

func (t *TMine) expectFind(lookfor, expected string) []interface{} {
	found, err := exec.LookPath(lookfor)
	if err != nil {
		return t.MyErrorf("Failed to find `%s`, expected to find it as `%s` but got an error instead: `%s`", lookfor, expected, err)
	}

	found = strings.ToUpper(found)
	expected = strings.ToUpper(expected)
	if found != expected { // && ( != strings.ToUpper(expected)) {
		return t.MyErrorf("Failed to find `%s`, expected to find it as `%s` but got `%s` instead.", lookfor, expected, found)
	}

	return t.ensureExitsAsFile(found)
}

//as file or symlink to file, even if symlink is broken
func (t *TMine) ensureExitsAsFile(fname string) []interface{} {
	finfo, err := os.Stat(fname)
	if err != nil {
		return t.MyErrorf("Expected `%s` to exist, error: `%s`", fname, err)
	}
	if !finfo.IsDir() {
		//could be symlink file
		//TODO: test if searched file is symlinkd or symlinkfile or dir or normal file, also readonly file, sysfile, hidden file + dirs
		//also when symlinkfile is broken (points to nothing)
		return nil
	}
	return t.MyErrorf("Expected `%s` to exist as a file, but it's got unexpected mode `%s`", fname, finfo.Mode())
}

func (t *TMine) setEnv(key, value string) {
	if err := os.Setenv(key, value); err != nil {
		t.Fatal(err)
	}

	if now_val := os.Getenv(key); value != now_val {
		t.Fatalf("Couldn't set `%s` env var, \ncurrent value: \n`%s`, \nwanted value: \n`%s`", key, now_val, value)
	}
}
