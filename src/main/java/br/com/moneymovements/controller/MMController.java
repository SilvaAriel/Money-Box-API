package br.com.moneymovements.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	public Resource<Account> createAccount(String accname, double balance) throws OpenAccountException, UnableToDepositException, AccountNotFoundException, InsufficientBalanceException, CloseAccountException {
		Account acc = accountService.createAccount(accname, balance);
		Resource resource = new Resource<>(acc);
		Link self = linkTo(MMController.class).slash(acc.getAccountId()).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(acc.getAccountId(), null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(acc.getAccountId(), null)).withRel("withdraw");
		Link transfer = linkTo(MMController.class).slash(acc.getAccountId()).slash("transferTo").slash("accId").withRel("transfer");
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
	public ResponseEntity<Resource<Double>> balance(@PathVariable("accId") int id) throws CloseAccountException, UnableToDepositException, AccountNotFoundException, InsufficientBalanceException {
		double balance = accountService.getBalance(id);
		Resource resource = new Resource<>(balance);
		Link self = linkTo(MMController.class).slash(id).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(id, null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(id, null)).withRel("withdraw");
		Link transfer = linkTo(MMController.class).slash(id).slash("transferTo").slash("accId").withRel("transfer");
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

	@RequestMapping(value = "/{account}/deposit", method = RequestMethod.POST)
	public Resource<Movement> deposit(@PathVariable("account") int account, Movement mov)
			throws UnableToDepositException, AccountNotFoundException, InsufficientBalanceException, CloseAccountException {
		Movement movement = this.accountService.deposit(mov);
		Resource resource = new Resource<>(mov);

		Link self = linkTo(MMController.class).slash(account).withSelfRel();
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(account, null)).withRel("withdraw");
		Link transfer = linkTo(MMController.class).slash(account).slash("transferTo").slash("accId").withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(account).slash("balance").slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(account).slash("balance").slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(account)).withRel("close");
		resource.add(self, withdraw, transfer, movementByDate, movementByValue, close);
		return resource;
	}

	@RequestMapping(value = "/{account}/withdraw", method = RequestMethod.POST)
	public Resource<Movement> withdraw(@PathVariable("account") int id, Movement mov)
			throws InsufficientBalanceException, AccountNotFoundException, UnableToDepositException, CloseAccountException {
		Movement movement = this.accountService.withdraw(mov);
		
		Resource resource = new Resource<>(movement);
		
		Link self = linkTo(MMController.class).slash(id).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(id, null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(id, null)).withRel("withdraw");
		Link transfer = linkTo(MMController.class).slash(id).slash("transferTo").slash("accId").withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(id).slash("balance").slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(id).slash("balance").slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(id)).withRel("close");
		if (movement.getAccount().getBalance() > 0) {
			resource.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resource.add(self, deposit, movementByDate, movementByValue, close);
		}
		
		return resource;
	}

	
	@RequestMapping(value = "/{account}/transferTo/{accDestination}", method = RequestMethod.POST)
	public Resource<Movement> transfer(@PathVariable("account") int accSource, @PathVariable("accDestination") int destAccount,
			Movement mov) throws InsufficientBalanceException, UnableToDepositException, AccountNotFoundException,
			SameAccountException, CloseAccountException {
		
		Movement movement = this.accountService.transfer(accSource, destAccount, mov);
		
		Resource resource = new Resource<>(movement);
		
		Link self = linkTo(MMController.class).slash(accSource).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(accSource, null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(accSource, null)).withRel("withdraw");
		Link transfer = linkTo(MMController.class).slash(accSource).slash("transferTo").slash("accId").withRel("transfer");
		Link movementByDate = linkTo(MMController.class).slash(accSource).slash("balance").slash("movement").slash("sort?by=date").withRel("movement_date");
		Link movementByValue = linkTo(MMController.class).slash(accSource).slash("balance").slash("movement").slash("sort?by=value").withRel("movement_value");
		Link close = linkTo(methodOn(MMController.class).closeAccount(accSource)).withRel("close");
		if (movement.getAccount().getBalance() > 0) {
			resource.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resource.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resource;
		
	}

	@RequestMapping(value = "/{account}/balance/movement", method = RequestMethod.GET)
	public Resources<List<Movement>> movement(@PathVariable("account") int account) throws AccountNotFoundException, CloseAccountException, InsufficientBalanceException, UnableToDepositException {
		
		Account acc = this.accountService.findAccount(account);
		
		List<Movement> movements = this.accountService.getAllMovementsByAccount(account);
		
		Resources resources = new Resources<>(movements);
		
		Link self = linkTo(MMController.class).slash(account).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(account, null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(account, null)).withRel("withdraw");
		Link transfer = linkTo(MMController.class).slash(account).slash("transferTo").slash("accId").withRel("transfer");
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
	public Resources<List<Movement>> sortMovement(@PathVariable("account") int account, String by) throws UnableToDepositException, AccountNotFoundException,InsufficientBalanceException, CloseAccountException {
		Account acc = this.accountService.findAccount(account);
		
		List<Movement> movements = this.accountService.getAllMovementsByAccountSorted(account, by);
		
		Resources resources = new Resources<>(movements);
		
		Link self = linkTo(MMController.class).slash(account).withSelfRel();
		Link deposit = linkTo(methodOn(MMController.class).deposit(account, null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(MMController.class).withdraw(account, null)).withRel("withdraw");
		Link transfer = linkTo(MMController.class).slash(account).slash("transferTo").slash("accId").withRel("transfer");
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
