package br.com.fullcycle.hexagonal.application.usecases;

public abstract class UnityUseCase<INPUT> {

    public abstract void execute(INPUT input);
}