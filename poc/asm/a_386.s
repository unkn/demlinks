
//this code is from here: https://groups.google.com/d/msg/golang-nuts/q01oIascmiU/wuoqrF_TOqgJ
//thanks to Jonathan Barnard

TEXT ·FlipByte(SB),7,$0
		MOVL	addr+0(FP), BP // This gets the address of the byte pointed to. 
    MOVL	0(BP), DX // This stores the byte in DX 
    MOVL	$8, CX // Use the CX register as a loop counter 
    XORL	AX, AX // Clear the AX register, to store the result in

loop: // This is a label to be jumped to.
		SHLB	$1, DX // Shift the byte in DX right, pushing the leftmost bit into the carry flag 
		RCRB	$1, AX // Rotate with carry the value in AX to the left, pushing the bit in the carry flag to the leftmost position in AX. 
    DECB  CX // Decrement the counter by one
    JNZ	loop // Jump to the loop label if the counter isn't zero

MOVL AX, 0(BP) //Move the result back into the address of the byte pointed to (modify the value pointed to by the pointer passed into the function).
RET //Return from the function
