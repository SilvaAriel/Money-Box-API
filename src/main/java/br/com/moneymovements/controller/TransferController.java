package br.com.moneymovements.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.service.AccountService;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

	@Autowired
	private AccountService accountService;

	@PostMapping()
	public Resource<Movement> transfer(@RequestBody Movement mov) throws InsufficientBalanceException,
			UnableToDepositException, AccountNotFoundException, SameAccountException, CloseAccountException {

		Movement movement = this.accountService.transfer(mov);

		Resource resource = new Resource<>(mov);

		Link self = linkTo(MMController.class).slash(movement.getAccount().getAccountId()).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(movement.getAccount().getAccountId()).slash("balance")
				.slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(movement.getAccount().getAccountId()).slash("balance")
				.slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(movement.getAccount().getAccountId()))
				.withRel("close");
		if (movement.getAccount().getBalance() > 0) {
			resource.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resource.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resource;

	}

}
