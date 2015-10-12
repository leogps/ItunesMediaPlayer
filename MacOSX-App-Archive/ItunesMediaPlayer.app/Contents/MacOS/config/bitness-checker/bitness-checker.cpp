#include "stdafx.h"
#include <iostream>
#include "comutil.h"

#define RESPONSE_32_BIT "32"
#define RESPONSE_64_BIT "64"

using namespace std;

typedef BOOL (WINAPI *IW64PFP)(HANDLE, BOOL *);

int main(int argc, char **argv){

	BOOL res = FALSE;
    IW64PFP IW64P = (IW64PFP)GetProcAddress(
			GetModuleHandle(L"kernel32"), "IsWow64Process");

    if(IW64P != NULL){
        IW64P(GetCurrentProcess(), &res);
    }

	cout << ((res) ? RESPONSE_64_BIT : RESPONSE_32_BIT) << endl;

	return 0;
	
}
