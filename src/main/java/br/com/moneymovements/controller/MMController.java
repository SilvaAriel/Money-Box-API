package br.com.moneymovements.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping("")
public class MMController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping("/account/{id}")
	public Account findAccount(@PathVariable("id") int id) throws AccountNotFoundException {
		return accountService.findAccount(id);
	}
	
	@GetMapping("/accounts")
	public List<Account> allAccounts() {
		return accountService.findAllAccounts();
	}

	@GetMapping("/openaccount")
	public Resource<Account> createAccount(String accname, double balance) throws OpenAccountException, UnableToDepositException, AccountNotFoundException, InsufficientBalanceException, CloseAccountException, SameAccountException {
		Account acc = accountService.createAccount(accname, balance);
		Resource resource = new Resource<>(acc);
		Link self = linkTo(MMController.class).slash(acc.getAccountId()).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link close = linkTo(methodOn(MMController.class).closeAccount(acc.getAccountId())).withRel("close");
		if (balance > 0) {
			resource.add(self, deposit, withdraw, transfer, close);
		} else {
			resource.add(self, deposit, close);
		}
		return resource;
	}

	@GetMapping("/{accId}/closeaccount")
	public ResponseEntity<Boolean> closeAccount(@PathVariable("accId") int id)
			throws CloseAccountException, AccountNotFoundException {
		boolean acc = accountService.closeAccount(id);
		return new ResponseEntity<>(acc, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{accId}/balance", method = RequestMethod.GET)
	public ResponseEntity<Resource<Double>> balance(@PathVariable("accId") int id) throws CloseAccountException, UnableToDepositException, AccountNotFoundException, InsufficientBalanceException, SameAccountException {
		double balance = accountService.getBalance(id);
		Resource resource = new Resource<>(balance);
		Link self = linkTo(MMController.class).slash(id).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(id).slash("balance").slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(id).slash("balance").slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(id)).withRel("close");
		if (balance > 0) {
			resource.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resource.add(self, deposit, movementByDate, movementByValue, close);
		}
		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

	@RequestMapping(value = "/deposit", method = RequestMethod.POST, consumes = {"application/json;charset=UTF-8"}, produces={"application/json;charset=UTF-8"})
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

	@RequestMapping(value = "/withdraw", method = RequestMethod.POST)
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

	
	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	public Resource<Movement> transfer(@RequestBody Movement mov) throws InsufficientBalanceException, UnableToDepositException, AccountNotFoundException,
	SameAccountException, CloseAccountException {
		
		Movement movement = this.accountService.transfer(mov);
		
		Resource resource = new Resource<>(mov);
		
		Link self = linkTo(MMController.class).slash(movement.getAccount().getAccountId()).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(movement.getAccount().getAccountId()).slash("balance").slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(movement.getAccount().getAccountId()).slash("balance").slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(movement.getAccount().getAccountId())).withRel("close");
		if (movement.getAccount().getBalance() > 0) {
			resource.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resource.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resource;
		
	}

	@RequestMapping(value = "/{account}/balance/movement", method = RequestMethod.GET)
	public Resources<List<Movement>> movement(@PathVariable("account") int account) throws AccountNotFoundException, CloseAccountException, InsufficientBalanceException, UnableToDepositException, SameAccountException {
		
		Account acc = this.accountService.findAccount(account);
		
		List<Movement> movements = this.accountService.getAllMovementsByAccount(account);
		
		Resources resources = new Resources<>(movements);
		
		Link self = linkTo(MMController.class).slash(account).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(account).slash("balance").slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(account).slash("balance").slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(account)).withRel("close");
		if (acc.getBalance() > 0) {
			resources.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resources.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resources;
	}
	
	@RequestMapping(value = "/{account}/balance/movement/sort", method = RequestMethod.GET)
	public Resources<List<Movement>> sortMovement(@PathVariable("account") int account, String by) throws UnableToDepositException, AccountNotFoundException,InsufficientBalanceException, CloseAccountException, SameAccountException {
		Account acc = this.accountService.findAccount(account);
		
		List<Movement> movements = this.accountService.getAllMovementsByAccountSorted(account, by);
		
		Resources resources = new Resources<>(movements);
		
		Link self = linkTo(MMController.class).slash(account).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(MMController.class).transfer(null)).withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(account).slash("balance").slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(account).slash("balance").slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(account)).withRel("close");
		if (acc.getBalance() > 0) {
			resources.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resources.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resources;
		
	}

	public void exportCSV() {

	}

}
