package br.com.moneymovements.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.service.AccountService;

@RestController
@RequestMapping("/api/movement")
public class MovementController {

	@Autowired
	private AccountService accountService;

	@GetMapping(params = { "id" })
	public Resources<List<Movement>> movement(@RequestParam(value = "id") int acc) throws AccountNotFoundException,
			CloseAccountException, InsufficientBalanceException, UnableToDepositException, SameAccountException {

		Account account = this.accountService.findAccount(acc);

		List<Movement> movements = this.accountService.getAllMovementsByAccount(acc);

		Resources resources = new Resources<>(movements);

		Link self = linkTo(methodOn(AccountController.class).getAccount(account.getAccountId())).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(TransferController.class).transfer(null)).withRel("transfer");
		Link close = linkTo(methodOn(AccountController.class).closeAccount(account.getAccountId())).withRel("close");
		Link movementByDate = linkTo(methodOn(MovementController.class).movement(account.getAccountId(), "date")).withRel("movement_by_date");
		Link movementByValue = linkTo(methodOn(MovementController.class).movement(account.getAccountId(), "value")).withRel("movement_by_value");
		if (account.getBalance() > 0) {
			resources.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resources.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resources;
	}

	@GetMapping(params = { "id", "by" })
	public Resources<List<Movement>> movement(@RequestParam(value = "id") int acc,
			@RequestParam(value = "by") String by) throws UnableToDepositException, AccountNotFoundException,
			InsufficientBalanceException, CloseAccountException, SameAccountException {
		Account account = this.accountService.findAccount(acc);

		List<Movement> movements = this.accountService.getAllMovementsByAccountSorted(acc, by);

		Resources resources = new Resources<>(movements);

		Link self = linkTo(methodOn(AccountController.class).getAccount(account.getAccountId())).withSelfRel();
		Link deposit = linkTo(methodOn(DepositController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(WithdrawController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link movementByDate = linkTo(methodOn(MovementController.class).movement(account.getAccountId(), "date")).withRel("movement_by_date");
		Link movementByValue = linkTo(methodOn(MovementController.class).movement(account.getAccountId(), "value")).withRel("movement_by_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(account.getAccountId())).withRel("close");
		if (account.getBalance() > 0) {
			resources.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resources.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resources;

	}

}
