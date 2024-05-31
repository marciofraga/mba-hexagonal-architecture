package br.com.fullcycle.hexagonal.application;

public abstract class UnityUseCase<INPUT> {

    public abstract void execute(INPUT input);
}