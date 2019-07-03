package br.com.moneymovements.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.OpenAccountException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@GetMapping(params = {"id"})
	public Account getAccount(@RequestParam (value="id") int id) throws AccountNotFoundException {
		return accountService.findAccount(id);
	}
	
	@GetMapping()
	public List<Account> getAccount() throws AccountNotFoundException {
		return accountService.findAllAccounts();
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = {
			"application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public Resource<Movement> deposit(@RequestBody Account acc) throws UnableToDepositException,
			AccountNotFoundException, InsufficientBalanceException, CloseAccountException, SameAccountException, OpenAccountException {

		Account account = accountService.createAccount(acc.getName(), acc.getBalance());
		Resource resource = new Resource<>(account);
		Link self = linkTo(MMController.class).slash(acc.getAccountId()).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link close = linkTo(methodOn(MMController.class).closeAccount(acc.getAccountId())).withRel("close");
		if (account.getBalance() > 0) {
			resource.add(self, deposit, withdraw, transfer, close);
		} else {
			resource.add(self, deposit, close);
		}
		return resource;

	}
	
	@PatchMapping()
	public ResponseEntity<Boolean> closeAccount(@RequestParam (value="id") int id)
			throws CloseAccountException, AccountNotFoundException {
		boolean acc = accountService.closeAccount(id);
		return new ResponseEntity<>(acc, HttpStatus.OK);
	}
}