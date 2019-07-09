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

import br.com.moneymovements.converter.DozerConverter;
import br.com.moneymovements.domain.Account;
import br.com.moneymovements.domain.Movement;
import br.com.moneymovements.exception.AccountNotFoundException;
import br.com.moneymovements.exception.CloseAccountException;
import br.com.moneymovements.exception.InsufficientBalanceException;
import br.com.moneymovements.exception.SameAccountException;
import br.com.moneymovements.exception.UnableToDepositException;
import br.com.moneymovements.service.AccountService;
import br.com.moneymovements.vo.MovementVO;

@RestController
@RequestMapping("/api/withdraw")
public class WithdrawController {
	
	@Autowired
	private AccountService accountService;

	@PostMapping(consumes = {"application/json;charset=UTF-8"}, produces={"application/json;charset=UTF-8"})
	public Resource<MovementVO> withdraw(@RequestBody Movement mov)
			throws InsufficientBalanceException, AccountNotFoundException, UnableToDepositException, CloseAccountException, SameAccountException {
		Movement movement = this.accountService.withdraw(mov);
		Account account = this.accountService.findAccount(movement.getAccount().getAccountId());
		MovementVO movementVO = DozerConverter.parseObject(movement, MovementVO.class);
		Resource resource = new Resource<>(movementVO);
		
		Link self = linkTo(methodOn(AccountController.class).getAccount(movement.getAccount().getAccountId())).withSelfRel();
		Link deposit = linkTo(methodOn(DepositController.class).deposit(null)).withRel("deposit");
		Link withdraw = linkTo(methodOn(WithdrawController.class).withdraw(null)).withRel("withdraw");
		Link transfer = linkTo(methodOn(TransferController.class).transfer(null)).withRel("transfer");
		Link close = linkTo(methodOn(AccountController.class).closeAccount(movement.getAccount().getAccountId())).withRel("close");
		Link movementByDate = linkTo(methodOn(MovementController.class).movement(movement.getAccount().getAccountId(), "date")).withRel("movement_by_date");
		Link movementByValue = linkTo(methodOn(MovementController.class).movement(movement.getAccount().getAccountId(), "value")).withRel("movement_by_value");
		if (account.getBalance() > 0) {
			resource.add(self, deposit, withdraw, transfer, movementByDate, movementByValue, close);
		} else {
			resource.add(self, deposit, movementByDate, movementByValue, close);
		}
		
		return resource;
	}
	
}
