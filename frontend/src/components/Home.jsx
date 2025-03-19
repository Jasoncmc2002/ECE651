import React, { Component } from 'react';
import { connect } from 'react-redux';
import ContendCard from './contents/ContentCard';
import Brand from '../images/icon-with-name.png';
import { Link } from 'react-router-dom';
import ACTIONS from '../redux/actions';
import { BoxArrowInRight, BoxArrowRight, Journal, PersonAdd, PersonGear, Gear, Pencil, Folder } from 'react-bootstrap-icons';

class Home extends Component {
    state = {}

    handlePermissionRender = () => {
        if (this.props.permission > 1) {
            return (
                <div className="container-fluid">
                    <div className="row mb-3">
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/problem_set/student_view/">
                                    My Problem Set
                                    <Pencil />
                                </Link>
                            </h4>
                        </div>
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/problem_manage/">
                                    Problem Management
                                    <Journal />
                                </Link>
                            </h4>
                        </div>
                    </div>
                    <div className="row mb-3">
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/set_manage/">
                                    Problem Set Management
                                    <Folder />
                                </Link>
                            </h4>
                        </div>
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/user_manage/">
                                    User Management
                                    <PersonGear />
                                </Link>
                            </h4>
                        </div>
                    </div>
                    <div className="row mb-3">
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/" onClick={() => this.props.logout()}>
                                    Sign Out
                                    <BoxArrowRight />
                                </Link>
                            </h4>
                        </div>
                    </div>
                </div>
            );
        } else if (this.props.permission > 0) {
            return (
                <div className="container-fluid">
                    <div className="row mb-3">
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/problem_set/student_view/">
                                    My Problem Set
                                    <Pencil />
                                </Link>
                            </h4>
                        </div>
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/problem_manage/">
                                    Problem Management
                                    <Journal />
                                </Link>
                            </h4>
                        </div>
                    </div>
                    <div className="row mb-3">
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/set_manage/">
                                    Problem Set Management
                                    <Folder />
                                </Link>
                            </h4>
                        </div>
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/" onClick={() => this.props.logout()}>
                                    Sign Out
                                    <BoxArrowRight />
                                </Link>
                            </h4>
                        </div>
                    </div>
                </div>
            );
        } else {
            return (
                <div className="container-fluid">
                    <div className="row mb-3">
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/problem_set/student_view/">
                                    My Problem Set
                                    <Pencil />
                                </Link>
                            </h4>
                        </div>
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/" onClick={() => this.props.logout()}>
                                    Sign Out
                                    <BoxArrowRight />
                                </Link>
                            </h4>
                        </div>
                    </div>
                </div>
            );
        }
    }

    handleAccountRender = () => {
        const style = { "--bs-icon-link-transform": "rotate(360deg)" };
        if (this.props.is_login) {
            let now = new Date().getHours();
            let greetings = now < 12 ? "Good morning" : now < 18 ? "Good afternoon" : "Good evening";

            return (
                <React.Fragment>
                    <h4 className="text-center mb-3">{greetings}, {this.props.name}</h4>
                    <hr />
                    {this.handlePermissionRender()}
                </React.Fragment>
            );
        } else {
            let now = new Date().getHours();
            let greetings = now < 12 ? "Good morning" : now < 18 ? "Good afternoon" : "Good evening";
            return (
                <React.Fragment>
                    <h4 className="text-center mb-3">{greetings}, welcome to Python Programming Platform</h4>
                    <hr />
                    <div className="container-fluid">
                        <div className="row">
                            <div className="col">
                                <h4 className="text-center">
                                    <Link className='icon-link icon-link-hover' to="/login/">
                                        Sign In
                                        <BoxArrowInRight />
                                    </Link>
                                </h4>
                            </div>
                            <div className="col">
                                <h4 className="text-center">
                                    <Link className='icon-link icon-link-hover' to="/register/">
                                        Register
                                        <PersonAdd />
                                    </Link>
                                </h4>
                            </div>
                        </div>
                    </div>
                </React.Fragment>
            );
        }
    }

    render() {
        return (
            <div className="container">
                <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                    <div className="col col-md-7">
                        <div className="container-fluid">
                            <ContendCard>
                                <div className='d-flex justify-content-center mb-3'>
                                    <img src={Brand} className='img-fluid' alt="Brand" style={{ maxWidth: "50%" }}></img>
                                </div>
                                {this.handleAccountRender()}
                            </ContendCard>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

const mapStateToProps = (state, props) => {
    return {
        ...props,
        user_id: state.user_id,
        username: state.username,
        name: state.name,
        permission: state.permission,
        token: state.token,
        is_login: state.is_login,
    };
};

const mapDispatchToProps = {
    logout: () => {
        return {
            type: ACTIONS.LOGOUT
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Home);