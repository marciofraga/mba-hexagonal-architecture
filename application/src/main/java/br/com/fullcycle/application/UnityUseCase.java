package br.com.fullcycle.application;

public abstract class UnityUseCase<INPUT> {

    public abstract void execute(INPUT input);
}