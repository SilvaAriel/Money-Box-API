package br.com.moneymovements.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.service.AccountService;

@RestController
@RequestMapping("")
public class MMController {
	
	@Autowired
	private AccountService accountService;

	@GetMapping("/createaccount")
	public Account createAccount(String accname, double balance) {
		return accountService.createAccount(accname, balance);
	}

	@GetMapping("/closeaccount")
	public boolean closeAccount(int id) {
		boolean acc = accountService.closeAccount(id);
		return acc;
	}
	
	@RequestMapping(value="/{accId}/balance", method=RequestMethod.GET)
	public double balance(@PathVariable("accId") int id) {
		return accountService.getBalance(id);
	}
	
	@RequestMapping(value="/{account}/deposit", method=RequestMethod.POST)
	public Movement deposit(@PathVariable("account") int account, Movement mov) {
		return this.accountService.deposit(mov);
	}
	
	@RequestMapping(value="/{account}/withdraw", method=RequestMethod.POST)
	public Movement withdraw(@PathVariable("account") int account, Movement mov) {
		return this.accountService.withdraw(mov);
	}
	
	@RequestMapping(value="/{account}/transferTo/{accDestination}", method=RequestMethod.POST)
	public Movement transfer(@PathVariable("account") int accSource, @PathVariable("accDestination") int accDestination, Movement mov) {
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
