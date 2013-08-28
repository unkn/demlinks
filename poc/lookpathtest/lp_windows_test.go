package exec_test

import (
	"container/list"
	"errors"
	// "fmt"
	"io/ioutil"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"testing"
	"time"
)

const PATHvar = "PATH"
const PATH_separator = string(os.PathListSeparator)
const folders_separator = string(os.PathSeparator)
const PATHEXT_separator = ";"
const PATHEXTvar = "PATHEXT"
const comspecvar = "COMSPEC"

func TestLookPath1(t *testing.T) {
	println("testing...") //TODO: remove this line when all done
	//var myt TMine = TMine(*t)
	myt := (*TMine)(t)

	tmp, err1 := ioutil.TempDir("", "testWindowsLookPathTests")
	if err1 != nil {
		myt.Quitf("TempDir failed: %v", err1)
	} else {
		defer func() {
			//seen(kinda): see why this folder doesn't get removed when tests pass, but only when they fail https://groups.google.com/forum/#!topic/golang-nuts/lpg45Bjj5HI
			//workaround for issue: http://code.google.com/p/go/issues/detail?id=6266
			for err := errors.New("impossibiru"); err != nil; err = os.RemoveAll(tmp) {
				time.Sleep(100 * time.Millisecond)
			}
		}()
	}

	//going to try LookPath for comspec var first, this is probably not needed and we can do away with just the tmp dir part
	//but just wanted to have one real thing

	// myt.setEnv("COMSpec", "cmd.exe")
	comspec := os.Getenv(comspecvar)
	if comspec == "" {
		myt.Quitf("%s must be set", comspecvar)
	}

	dir, file := filepath.Split(comspec)
	// println(file)
	if dir == "" {
		myt.Quitf("Expected to have a path in %%%s%% 's value: `%s`", comspecvar, comspec)
	}

	//TODO: paths in PATH with & without suffix of "\\"
	//TODO: make one char be lowercase or uppercase in the file to look for, and also in PATH env var

	balddir := strings.TrimSuffix(dir, folders_separator)
	// paths := MyCollection.New(
	paths := []string{
		balddir,
		dir,
		os.Getenv(PATHvar),
		tmp,
		".",
		"",
		// )
	}

	//myt.ensureExitsAsFile(tmp)
	//done: should the entire test fail ie. FailNow() when one of them fails? hmm, maybe it's best that way
	//because otherwise there will be too many errors reported anyway

	//println(dir, filepath.Join(dir, file))
	myt.testFor(file, filepath.Join(dir, file), []string{
		balddir,
		dir,
		tmp,
		".",
		"",
		// )
	})
	_, _ = file, paths

	myt.testFor(file, filepath.Join(balddir, file), []string{
		dir,
		tmp,
		".",
		"",
		// )
	})

	//C:\folder1\folder2\file
	//C:\folder1\folder2\file.
	//C:\folder1\folder2\file.ext
	//C:\folder1\folder2\file.extension
	//C:\folder1\folder2\file.multi.dot.extension
	//C:\folder1\folder2\file.multi.dot.ext
	//C:\folder1\folder2\file.multi.dot.
	//replace dot with from "" until "dotext", so "", "d","do","dot","dote","dotex","dotext"
	//replace ext with from "" until "extension"
	//replace file with from "" until "fileover8chars"
	//also use spaces for each step in the above ie. for dot(above^) ""," ", "d", "d "," d"," d ", "do", " do","do ", " do " etc...
	//if testing.Short() then don't iterate that many variants
	//replace folder1 or 2 with same concept as for ext/dot/file
	//also do uppercase variants, maybe just all upper, all lower, and one char upper and rest lower (and reverse of this)
	//PATH should contain some inexisting folders to search into which are before and after the correct folder
	//sometimes PATH should contain only the correct folder(cf), sometimes only normal but not cf folder
	// and inexsiting folders and such combinations
	//and add to that each path element in pathlist would end or not with "\\"

	//TODO: try to make a bunch of 0 bytes files in tmp folder that we got, and dirs + symlinks / combinations
	//and then do try those as if they were to be found by LookPath, obvious set the PATH to point to tmp folder

	//TODO: use no ext tmp folder, and then an ext one, and then combine with ones with spaces in name and then in ext
	//TODO: and then try multiple dots in the name

	var f1 *os.File
	if f, err := ioutil.TempFile(tmp, "prefix"); err != nil {
		myt.Quitf("unable to create a temp file in temp folder `%s`, error: `%s`", tmp, err)
	} else {
		defer f.Close() //yep still need to close this before deleting my temp folder
		f1 = f
		// defer os.Remove(f) //no need
	}
	println(f1.Name())
	//filepath.Join(tmp,f.Name())
	//os.Symlink(tmp+, newname)

	//TODO: must check if os.Symlink is really not implemented on Windows, if it's not then try to implement it prior to this

}

type MyCollection struct {
	*list.List
}

// func NewCol(args ...[]interface{}) {
// 	for _, i := range args {

// 	}
// }

//TODO: remove this line://something I didn't know: https://groups.google.com/d/msg/golang-nuts/RA9be0XNvGc/kS8nD5hlAeYJ
func (t *MyCollection) GetACopy() *MyCollection {
	a := list.New()
	a.PushBackList(t.List)
	b := new(MyCollection)
	b.List = a
	return b
}

func (t *MyCollection) GetNewWithMovedToFront(newfront string) *MyCollection {
	a := t.GetACopy()
	e := new(list.Element)
	e.Value = newfront
	a.MoveToFront(e)
	return a
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

func (t *TMine) Quit(args ...interface{}) {
	t.Fatal(args...)
}

func (t *TMine) Quitf(format string, args ...interface{}) {
	t.Fatalf(format, args...)
}

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
		if !testing.Short() {
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
		t.Quitf("Failed to find `%s`, expected to find it as `%s` but got an error instead: `%s`", lookfor, expected, err)
	}

	found = strings.ToUpper(found)
	expected = strings.ToUpper(expected)
	if found != expected { // && ( != strings.ToUpper(expected)) {
		t.Quitf("Failed to find `%s`, expected to find it as `%s` but got `%s` instead.", lookfor, expected, found)
	}

	t.ensureExitsAsFile(found)
}

//as file or symlink to file, even if symlink is broken
func (t *TMine) ensureExitsAsFile(fname string) {
	finfo, err := os.Stat(fname)
	if err != nil {
		t.Quitf("Expected `%s` to exist, error: `%s`", fname, err)
	}
	if !finfo.IsDir() {
		//could be symlink file
		//TODO: test if searched file is symlinkd or symlinkfile or dir or normal file, also readonly file, sysfile, hidden file + dirs
		//also when symlinkfile is broken (points to nothing)
		return
	}
	t.Quitf("Expected `%s` to exist as a file, but it's a directory with this mode `%s`", fname, finfo.Mode())
}

func (t *TMine) setEnv(key, value string) {
	if err := os.Setenv(key, value); err != nil {
		t.Quit(err)
	}

	if now_val := os.Getenv(key); value != now_val {
		t.Quitf("Couldn't set `%s` env var, \ncurrent value: \n`%s`, \nwanted value: \n`%s`", key, now_val, value)
	}
}
