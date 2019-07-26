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

import br.com.moneymovements.converter.DozerConverter;
import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.service.AccountService;
import br.com.moneymovements.vo.AccountVO;
import br.com.moneymovements.vo.MovementVO;

@RestController
@RequestMapping("/api/movement")
public class MovementController {

	@Autowired
	private AccountService accountService;

	@GetMapping(params = { "id" })
	public Resources<List<MovementVO>> movement(@RequestParam(value = "id") int acc) throws AccountNotFoundException,
			CloseAccountException, InsufficientBalanceException, UnableToDepositException, SameAccountException {

		AccountVO accountVO = DozerConverter.parseObject(this.accountService.findAccount(acc), AccountVO.class);

		List<MovementVO> movementsVO = this.accountService.getAllMovementsByAccount(acc);

		Resources resources = new Resources<>(movementsVO);

		Link self = linkTo(methodOn(AccountController.class).getAccount(accountVO.getAccountId())).withSelfRel();
		Link deposit = linkTo(methodOn(DepositController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(WithdrawController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(TransferController.class).transfer(null)).withRel("transfer");
		Link close = linkTo(methodOn(AccountController.class).closeAccount(accountVO)).withRel("close");
		Link movementByDate = linkTo(methodOn(MovementController.class).movement(accountVO.getAccountId(), "date")).withRel("movement_by_date");
		Link movementByValue = linkTo(methodOn(MovementController.class).movement(accountVO.getAccountId(), "value")).withRel("movement_by_value");
		if (accountVO.getBalance() > 0) {
			resources.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resources.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resources;
	}

	@GetMapping(params = { "id", "by" })
	public Resources<List<MovementVO>> movement(@RequestParam(value = "id") int acc,
			@RequestParam(value = "by") String by) throws UnableToDepositException, AccountNotFoundException,
			InsufficientBalanceException, CloseAccountException, SameAccountException {
		
		AccountVO accountVO = DozerConverter.parseObject(this.accountService.findAccount(acc), AccountVO.class);

		List<MovementVO> movementsVO = this.accountService.getAllMovementsByAccountSorted(acc, by);

		Resources resources = new Resources<>(movementsVO);

		Link self = linkTo(methodOn(AccountController.class).getAccount(accountVO.getAccountId())).withSelfRel();
		Link deposit = linkTo(methodOn(DepositController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(WithdrawController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(TransferController.class).transfer(null)).withRel("transfer");
		Link movementByDate = linkTo(methodOn(MovementController.class).movement(accountVO.getAccountId(), "date")).withRel("movement_by_date");
		Link movementByValue = linkTo(methodOn(MovementController.class).movement(accountVO.getAccountId(), "value")).withRel("movement_by_value");
		Link close = linkTo(methodOn(AccountController.class).closeAccount(accountVO)).withRel("close");
		if (accountVO.getBalance() > 0) {
			resources.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resources.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resources;

	}

}
