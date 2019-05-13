package br.com.moneymovements.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
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

	@GetMapping("/openaccount")
	public Resource<Account> createAccount(String accname, double balance) throws OpenAccountException {
		Account acc = accountService.createAccount(accname, balance);
		//return new Resource<>(acc, linkTo(methodOn(Account.class).getAccountId()).withSelfRel());
		Link selfLink = linkTo(MMController.class).slash(acc.getAccountId()).withSelfRel();
		Link accBalance = linkTo(MMController.class).slash(acc.getAccountId()).slash("balance").withRel("account_balance");
		return new Resource<>(acc, selfLink, accBalance);
	}

	@GetMapping("/closeaccount")
	public boolean closeAccount(int id) throws CloseAccountException, AccountNotFoundException {
		boolean acc = accountService.closeAccount(id);
		return acc;
	}
	
	@RequestMapping(value="/{accId}/balance", method=RequestMethod.GET)
	public double balance(@PathVariable("accId") int id) throws CloseAccountException {
		return accountService.getBalance(id);
	}
	
	@RequestMapping(value="/{account}/deposit", method=RequestMethod.POST)
	public Resource<Movement> deposit(@PathVariable("account") int account, Movement mov) throws UnableToDepositException, AccountNotFoundException {
		Movement movement = this.accountService.deposit(mov);
		Link selfLink = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash(mov.getMovementId()).withSelfRel();
		Link accLink = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).withRel("account");
		Link accBalance = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash("balance").withRel("account_balance");
		Link accWithdraw = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash("withdraw").withRel("withdraw");
		Link accMovements = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash("balance").slash("movement").withRel("movements");
		Link accMovementsSortedByDate = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash("balance").slash("movement?sort=date").withRel("movements_sort_by_date");
		Link accMovementsSortedByValue = linkTo(MMController.class).slash(mov.getAccount().getAccountId()).slash("balance").slash("movement?sort=value").withRel("movements_sort_by_value");
		return new Resource<>(movement, selfLink, accLink, accBalance, accWithdraw, accMovements, accMovementsSortedByDate, accMovementsSortedByValue);
	}
	
	@RequestMapping(value="/{account}/withdraw", method=RequestMethod.POST)
	public Movement withdraw(@PathVariable("account") int account, Movement mov) throws InsufficientBalanceException, AccountNotFoundException {
		return this.accountService.withdraw(mov);
	}
	
	@RequestMapping(value="/{account}/transferTo/{accDestination}", method=RequestMethod.POST)
	public Movement transfer(@PathVariable("account") int accSource, @PathVariable("accDestination") int accDestination, Movement mov) throws InsufficientBalanceException, UnableToDepositException, AccountNotFoundException, SameAccountException {
		return this.accountService.transfer(accSource, accDestination, mov);
	}
	
	@RequestMapping(value="/{account}/balance/movement", method=RequestMethod.GET)
	public List<Movement> movement(@PathVariable("account") int account, String sort) {
		return this.accountService.getAllMovementsByAccount(account);
	}
	
	@RequestMapping(value="/{account}/balance/movement/sort", method=RequestMethod.GET)
	public List<Movement> sortMovement(@PathVariable("account") int account, String by) {
		return this.accountService.getAllMovementsByAccountSorted(account, by);
	}

	public void exportCSV() {

	}

}
