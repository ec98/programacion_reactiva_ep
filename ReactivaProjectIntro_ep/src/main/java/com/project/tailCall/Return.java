package com.project.tailCall;

record Return<T>(T valor) implements TailCall<T>{

	@Override
	public T eval() {
		// TODO Auto-generated method stub
		return valor; //retorna el valor final
	}

	@Override
	public TailCall<T> resume() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSuspend() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
