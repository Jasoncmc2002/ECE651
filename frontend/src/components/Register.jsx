import React, { Component } from 'react';
import $ from 'jquery';
import { useNavigate } from 'react-router-dom';
import ContendCard from './contents/ContentCard';
import BACKEND_ADDRESS_URL from "./config/BackendAddressURLConfig";

class Register extends Component {
    state = {
        username: "",
        password: "",
        password_confirm: "",
        error_message: "",

        is_loading: false,
    }

    handleRegister = (e) => {
        this.setState({
            error_message: "",
            is_loading: true,
        });
        e.preventDefault();
        // console.log("click");
        if (this.state.username === "") {
            this.setState({
                error_message: "Username cannot be empty",
                is_loading: false,
            })
        } else if (this.state.password === "") {
            this.setState({
                error_message: "Password cannot be empty",
                is_loading: false,
            })
        } else if (this.state.password_confirm === "") {
            this.setState({
                error_message: "Confirm password cannot be empty",
                is_loading: false,
            })
        } else if (this.state.password !== this.state.password_confirm) {
            this.setState({
                error_message: "The passwords you entered twice do not match",
                is_loading: false,
            })
        } else {
            // console.log("register");
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/user/account/register/",
                type: "POST",
                data: {
                    username: this.state.username,
                    password: this.state.password,
                    confirmedPassword: this.state.password_confirm
                },
                success: (resp) => {
                    console.log(resp)
                    if (resp.error_message === "success") {
                        this.props.navigate(-1);
                    } else {
                        this.setState({
                            error_message: resp.error_message,
                            is_loading: false,
                        });
                    }
                },
                error: (resp) => {
                    console.log(resp)
                }
            });
        }
    }

    handleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            );
        }
    }

    render() {
        return (
            <div className="container">
                <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                    <div className="col col-md-5" >
                        <ContendCard>
                            <h4 className='text-center'>Register</h4>
                            <hr className='m-1' />
                            <form>
                                <div className="mb-3">
                                    <label htmlFor="username" className="form-label">Username</label>
                                    <input type="text" className="form-control" id="username" name="username" onChange={(e) => { this.setState({ username: e.target.value }) }} />
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="password" className="form-label">Password</label>
                                    <input type="password" className="form-control" id="password" name="password" onChange={(e) => { this.setState({ password: e.target.value }) }} />
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="password_confirm" className="form-label">Confirm Password</label>
                                    <input type="password" className="form-control" id="password_confirm" name="password_confirm" onChange={(e) => { this.setState({ password_confirm: e.target.value }) }} />
                                </div>
                                <div className="error-message mb-3" style={{ height: "2rem", color: "red" }}>
                                    {this.state.error_message}
                                </div>
                                <button type="submit" className="btn btn-primary" style={{ width: "100%" }} onClick={(e) => this.handleRegister(e)}>
                                    {this.handleLoadingRender()}
                                    Register
                                </button>
                            </form>
                        </ContendCard>
                    </div>
                </div>
            </div>
        );
    }
}

export default (props) => (
    <Register
        {...props}
        navigate={useNavigate()}
    />
);