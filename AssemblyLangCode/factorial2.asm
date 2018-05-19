.main:
	.encode mov r1,5
	.loop:
		.print r1
		.encode sub r1,r1,1
		.encode cmp r1,0
		.encode bgt .loop
