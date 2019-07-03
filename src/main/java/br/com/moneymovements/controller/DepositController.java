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
@RequestMapping("/api/deposit")
public class DepositController {
	
	@Autowired
	private AccountService accountService;

	@PostMapping(consumes = {"application/json;charset=UTF-8"}, produces={"application/json;charset=UTF-8"})
	public Resource<Movement> deposit(@RequestBody Movement mov)
			throws UnableToDepositException, AccountNotFoundException, InsufficientBalanceException, CloseAccountException, SameAccountException {
			
		Movement movement = this.accountService.deposit(mov);
		Resource resource = new Resource<>(mov);
		
		Link self = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).withSelfRel();
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash("balance").slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash("balance").slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(mov.getAccount().getAccountId())).withRel("close");
		resource.add(self, withdraw, transfer, movementByDate, movementByValue, close);
		return resource;
	}
	
}
