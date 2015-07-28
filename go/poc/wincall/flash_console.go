//code from: https://groups.google.com/d/msg/golang-nuts/3uMpaZ-m_kg/w3zNDYsAAy0J
//flashes the current console window, in Windows OS

//more info on interacting with Windows OS: https://github.com/MalcolmJSmith/Winhello/blob/master/Winhello.go

package main

import "syscall"
import "unsafe"
import "fmt"

//http://msdn.microsoft.com/en-us/library/ms679348%28v=vs.85%29.aspx
type FlashInfo struct {
	CbSize    uint32
	Hwnd      uintptr
	DwFlags   uint32
	UCount    uint32
	DwTimeout uint32
}

var Info *FlashInfo
var kernel32 = syscall.NewLazyDLL("kernel32.dll")
var user32 = syscall.NewLazyDLL("user32.dll")

//http://msdn.microsoft.com/en-us/library/ms679347%28VS.85%29.aspx
var FlashWindowEx = user32.NewProc("FlashWindowEx")
var GetConsoleWindow = kernel32.NewProc("GetConsoleWindow")

func Init() {
	Info = new(FlashInfo)
	Info.CbSize = uint32(unsafe.Sizeof(*Info))
	Info.Hwnd, _, _ = GetConsoleWindow.Call()
	Info.DwFlags = 1 //2
	Info.UCount = 10
	Info.DwTimeout = 0
}

func FlashWindow() {
	_, _, err := FlashWindowEx.Call(uintptr(unsafe.Pointer(Info)))
	if err != nil {
		fmt.Println(err)
	}
}

func main() {
	Init()
	FlashWindow()
}
