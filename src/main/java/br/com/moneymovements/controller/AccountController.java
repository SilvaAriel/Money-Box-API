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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.moneymovements.converter.DozerConverter;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.OpenAccountException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.service.AccountService;
import br.com.moneymovements.vo.AccountVO;

@RestController
@RequestMapping("/api/account")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@GetMapping(params = { "id" })
	public AccountVO getAccount(@RequestParam(value = "id") int id) throws AccountNotFoundException {
		return DozerConverter.parseObject(accountService.findAccount(id), AccountVO.class);
	}

	@GetMapping()
	public List<AccountVO> getAccount() throws AccountNotFoundException {
		return accountService.findAllAccounts();
	}

	@PostMapping(consumes = { "application/json;charset=UTF-8" }, produces = { "application/json;charset=UTF-8" })
	public ResponseEntity<Resource<AccountVO>> account(@RequestBody AccountVO acc)
			throws UnableToDepositException, AccountNotFoundException, InsufficientBalanceException,
			CloseAccountException, SameAccountException, OpenAccountException {

		AccountVO accountVO = accountService.createAccount(acc.getName(), acc.getBalance());
		Resource resource = new Resource<>(accountVO);
		Link self = linkTo(methodOn(AccountController.class).getAccount(accountVO.getAccountId())).withSelfRel();
		Link deposit = linkTo(methodOn(DepositController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(WithdrawController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(TransferController.class).transfer(null)).withRel("transfer");
		Link close = linkTo(methodOn(AccountController.class).closeAccount(accountVO.getAccountId())).withRel("close");
		if (accountVO.getBalance() > 0) {
			resource.add(self, deposit, withdraw, transfer, close);
		} else {
			resource.add(self, deposit, close);
		}
		return new ResponseEntity<>(resource, HttpStatus.CREATED);

	}

	@PatchMapping()
	public ResponseEntity<Boolean> closeAccount(@RequestParam(value = "id") int id)
			throws CloseAccountException, AccountNotFoundException {
		boolean acc = accountService.closeAccount(id);
		return new ResponseEntity<>(acc, HttpStatus.OK);
	}
}