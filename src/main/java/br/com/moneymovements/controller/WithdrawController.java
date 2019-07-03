package br.com.moneymovements.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.service.AccountService;

@RestController
@RequestMapping("/api/withdraw")
public class WithdrawController {
	
	@Autowired
	private AccountService accountService;

	@PostMapping()
	public Resource<Movement> withdraw(@RequestBody Movement mov)
			throws InsufficientBalanceException, AccountNotFoundException, UnableToDepositException, CloseAccountException, SameAccountException {
		Movement movement = this.accountService.withdraw(mov);
		
		Resource resource = new Resource<>(movement);
		
		Link self = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash("balance").slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash("balance").slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(mov.getAccount().getAccountId())).withRel("close");
		if (movement.getAccount().getBalance() > 0) {
			resource.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resource.add(self, deposit, movementByDate, movementByValue, close);
		}
		
		return resource;
	}
	
}
