package br.com.moneymovements.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.moneymovements.converter.DozerConverter;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.service.AccountService;
import br.com.moneymovements.vo.AccountVO;
import br.com.moneymovements.vo.MovementVO;

@CrossOrigin(origins = "https://moneybox1.herokuapp.com/")
@RestController
@RequestMapping("/api/transfer")
public class TransferController {

	@Autowired
	private AccountService accountService;

	@PostMapping(consumes = {"application/json;charset=UTF-8"}, produces={"application/json;charset=UTF-8"})
	public Resource<MovementVO> transfer(@RequestBody MovementVO movement) throws InsufficientBalanceException,
			UnableToDepositException, AccountNotFoundException, SameAccountException, CloseAccountException {

		MovementVO movementVO = this.accountService.transfer(movement);
		
		AccountVO accountVO = DozerConverter.parseObject(this.accountService.findAccount(movementVO.getAccount().getAccountId()), AccountVO.class);
		
		Resource resource = new Resource<>(movementVO);
		
		Link self = linkTo(methodOn(AccountController.class).getAccount(movementVO.getAccount().getAccountId())).withSelfRel();
		Link deposit = linkTo(methodOn(DepositController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(WithdrawController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(TransferController.class).transfer(null)).withRel("transfer");
		Link close = linkTo(methodOn(AccountController.class).closeAccount(accountVO)).withRel("close");
		Link movementByDate = linkTo(methodOn(MovementController.class).movement(movementVO.getAccount().getAccountId(), "date")).withRel("movement_by_date");
		Link movementByValue = linkTo(methodOn(MovementController.class).movement(movementVO.getAccount().getAccountId(), "value")).withRel("movement_by_value");
		if (accountVO.getBalance() > 0) {
			resource.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resource.add(self, deposit, movementByDate, movementByValue, close);
		}
		return resource;

	}

}
